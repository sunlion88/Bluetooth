package com.sunlion.bluetooth.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;


/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告
 * Created by Administrator on 2016/12/4 0004.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static CrashHandler INSTANCE;
    private Context mContext;

    /**
     * 保证只有一个CrashhHandler实例
     */
    private CrashHandler() {

    }

    /**
     * 获取CrashHandler实例,单例模式
     */
    private static CrashHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new CrashHandler();
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;

        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UnCaughtException发生时会转入该重写的方法来处理
     */
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果自定义的没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    public boolean handleException(Throwable ex) {
        if (ex == null || mContext == null)
            return false;
        final String crashReport = getCrashReport(mContext, ex);
        new Thread() {
            public void run() {
                Looper.prepare();
                File file = save2File(crashReport);
                sendAppCrashReport(mContext, crashReport, file);
                Looper.loop();
            }
        }.start();
        return true;
    }

    private File save2File(String crashReport) {
        String fileName = "crash-" + System.currentTimeMillis() + ".txt";
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                File dir = new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + File.separator + "crash");
                if (!dir.exists())
                    dir.mkdir();
                File file = new File(dir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(crashReport.toString().getBytes());
                fos.close();
                return file;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private void sendAppCrashReport(final Context context, final String crashReport, final File file) {
        if (null == context)
            return;
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("出错了");
        builder.setMessage("不好意思,出错了!");
        builder.setPositiveButton("上报",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            String[] tos = {"sunlion168@163.com"};
                            intent.putExtra(Intent.EXTRA_EMAIL,tos);

                            intent.putExtra(Intent.EXTRA_SUBJECT,
                                    "错误报告");
                            if(file !=null){
                                intent.putExtra(Intent.EXTRA_STREAM,
                                        Uri.fromFile(file));
                                intent.putExtra(Intent.EXTRA_TEXT,
                                        "请将此错误报告发送给我，以便我尽快修复此问题，谢谢合作！\n");
                            }else {
                                intent.putExtra(Intent.EXTRA_TEXT,
                                        "请将此错误报告发送给我，以便我尽快修复此问题，谢谢合作！\n"
                                                + crashReport);
                            }
                            intent.setType("text/plain");
                            intent.setType("message/rfc882");
                            intent.createChooser(intent,"Choose Email Client");
                            context.startActivity(intent);
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            dialog.dismiss();
                            //退出
                            android.os.Process.killProcess(android.os.Process
                            .myPid());
                            System.exit(1);
                        }
                    }
                });
        builder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            dialog.dismiss();
                            //退出
                            android.os.Process.killProcess(android.os.Process
                            .myPid());
                            System.exit(1);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
        dialog = builder.create();
        dialog.getWindow()
                .setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();

    }


    /**
     *获取APP崩溃异常报告
     * @param context
     * @param  ex
     */
    private String getCrashReport(Context context, Throwable ex) {
        PackageInfo pinfo = getPackageInfo(context);
        StringBuffer exceptionStr = new StringBuffer();
        exceptionStr.append("Version: " + pinfo.versionName + "("
                + pinfo.versionCode + ")\n");
        exceptionStr.append("Android: " + android.os.Build.VERSION.RELEASE
                + "(" + android.os.Build.MODEL + ")\n");
        exceptionStr.append("Exception: " + ex.getMessage() + "\n");
        StackTraceElement[] elements = ex.getStackTrace();
        for(int i=0;i<elements.length;i++){
            exceptionStr.append(elements[i].toString() + "\n");
        }
        return exceptionStr.toString();
    }

    /**
     * 获取APP安装包信息
     * @param context
     * @return
     */
    private PackageInfo getPackageInfo(Context context) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),0);
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        if(info == null){
            info = new PackageInfo();
        }
        return info;
    }
}
