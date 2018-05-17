package com.xiaolanba.passenger.common.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.framework.common.utils.IAppUtil;
import com.xiaolanba.passenger.module.main.activity.LanucherActivity;
import com.xlb.elect.cigar.R;

import java.util.List;

/**
 * 快捷方式入口
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class ShortcutUtil {

    public static boolean isNeedShortCut(Context cx) {
        boolean mShortNeeded = false;
        try {
            SharedPreferences preferences = cx.getSharedPreferences("yaya", Context.MODE_PRIVATE);
            String historyShort = preferences.getString("shortcut", "");
            String realShort = IAppUtil.getVersionName(cx);
            Editor editor = preferences.edit();
            editor.putString("shortcut", realShort);
            editor.commit();
            if ("".equals(historyShort)) {
                mShortNeeded = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mShortNeeded;
    }


    /**
     * 删除当前应用的桌面快捷方式
     *
     * @param cx
     */
    public static void delShortcut(Context cx) {
        try {
            Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
            //快捷方式的名称
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, cx.getString(R.string.app_name));
            /**删除和创建需要对应才能找到快捷方式并成功删除**/
            Intent intent = new Intent();
            intent.setClass(cx, cx.getClass());
            intent.setAction("android.intent.action.MAIN");
            intent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
            intent.addCategory("android.intent.category.LAUNCHER");
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
            cx.sendBroadcast(shortcut);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void addShortcut(Context cx) {
        try {
            Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            // 获取当前应用名称
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, cx.getString(R.string.app_name));
            shortcut.putExtra("duplicate", false);//设置是否重复创建
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            //TODO
            intent.setClass(cx, LanucherActivity.class);//设置第一个页面
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
            Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(cx, R.drawable.logo);
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
            cx.sendBroadcast(shortcut);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 为程序创建桌面快捷方式
     */
//	public static  void addShortcut(Context cx){  
//		Intent intent = new Intent();   
//        intent.setClass(cx, cx.getClass());    
//       /*以下两句是为了在卸载应用的时候同时删除桌面快捷方式*/  
//        intent.setAction("android.intent.action.MAIN");    
//        intent.addCategory("android.intent.category.LAUNCHER");    
//         
//        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");  
//        // 不允许重建  
//        shortcut.putExtra("duplicate", false);  
//        // 设置名字  
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,cx.getString(R.string.app_name));  
//        // 设置图标  
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,Intent.ShortcutIconResource.fromContext(cx, R.drawable.logo));  
//        // 设置意图和快捷方式关联程序  
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT,intent);  
//	          
//        cx.sendBroadcast(shortcut);  
//	}  


    private static String AUTHORITY = null;

    public static boolean isShortCutExist(Context context) {

        String title = context.getString(R.string.app_name);

        boolean isInstallShortcut = false;

        if (null == context || TextUtils.isEmpty(title))
            return isInstallShortcut;

        if (TextUtils.isEmpty(AUTHORITY))
            AUTHORITY = getAuthorityFromPermission(context);

        final ContentResolver cr = context.getContentResolver();

        if (!TextUtils.isEmpty(AUTHORITY)) {
            try {
                final Uri CONTENT_URI = Uri.parse(AUTHORITY);

                Cursor c = cr.query(CONTENT_URI, new String[]{"title",
                                "iconResource"}, "title=?", new String[]{title},
                        null);

                // XXX表示应用名称。
                if (c != null && c.getCount() > 0) {
                    isInstallShortcut = true;
                }
                if (null != c && !c.isClosed())
                    c.close();
            } catch (Exception e) {
                Log.e("isShortCutExist", e.getMessage());
            }

        }
        return isInstallShortcut;

    }

    public static String getCurrentLauncherPackageName(Context context) {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo res = context.getPackageManager()
                .resolveActivity(intent, 0);
        if (res == null || res.activityInfo == null) {
            // should not happen. A home is always installed, isn't it?
            return "";
        }
        if (res.activityInfo.packageName.equals("android")) {
            return "";
        } else {
            return res.activityInfo.packageName;
        }
    }

    public static String getAuthorityFromPermissionDefault(Context context) {

        return getThirdAuthorityFromPermission(context,
                "com.android.launcher.permission.READ_SETTINGS");
    }

    public static String getThirdAuthorityFromPermission(Context context,
                                                         String permission) {
        if (TextUtils.isEmpty(permission)) {
            return "";
        }

        try {
            List<PackageInfo> packs = context.getPackageManager()
                    .getInstalledPackages(PackageManager.GET_PROVIDERS);
            if (packs == null) {
                return "";
            }
            for (PackageInfo pack : packs) {
                ProviderInfo[] providers = pack.providers;
                if (providers != null) {
                    for (ProviderInfo provider : providers) {
                        if (permission.equals(provider.readPermission)
                                || permission.equals(provider.writePermission)) {
                            if (!TextUtils.isEmpty(provider.authority)// 精准匹配launcher.settings，再一次验证
                                    && (provider.authority)
                                    .contains(".launcher.settings"))
                                return provider.authority;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getAuthorityFromPermission(Context context) {
        // 获取默认
        String authority = getAuthorityFromPermissionDefault(context);
        // 获取特殊第三方
        if (authority == null || authority.trim().equals("")) {
            String packageName = getCurrentLauncherPackageName(context);
            packageName += ".permission.READ_SETTINGS";
            authority = getThirdAuthorityFromPermission(context, packageName);
        }
        // 还是获取不到，直接写死
        if (TextUtils.isEmpty(authority)) {
            int sdkInt = android.os.Build.VERSION.SDK_INT;
            if (sdkInt < 8) { // Android 2.1.x(API 7)以及以下的
                authority = "com.android.launcher.settings";
            } else if (sdkInt < 19) {// Android 4.4以下
                authority = "com.android.launcher2.settings";
            } else {// 4.4以及以上
                authority = "com.android.launcher3.settings";
            }
        }
        authority = "content://" + authority + "/favorites?notify=true";
        return authority;

    }


}