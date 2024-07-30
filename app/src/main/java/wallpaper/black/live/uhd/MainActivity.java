package wallpaper.black.live.uhd;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import wallpaper.black.live.uhd.AppUtils.AppUtility;
import wallpaper.black.live.uhd.AppUtils.FirebaseEventManager;
import wallpaper.black.live.uhd.AppUtils.InAppPreference;
import wallpaper.black.live.uhd.AppUtils.LoggerCustom;
import wallpaper.black.live.uhd.AppUtils.WallpaperType;
import wallpaper.black.live.uhd.Fragments.CategoryFragment;
import wallpaper.black.live.uhd.Fragments.FeatureFragment;
import wallpaper.black.live.uhd.Fragments.HomeFragment;
import wallpaper.black.live.uhd.Fragments.DoubleWallpaperFragment;
import wallpaper.black.live.uhd.Model.IModel;
import wallpaper.black.live.uhd.databinding.ActivityMainBinding;
import wallpaper.black.live.uhd.retrofit.RetroApiRequest;
import wallpaper.black.live.uhd.retrofit.RetroCallbacks;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ViewPagerAdapter pagerAdapter;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    InAppPreference inAppPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //temp
        inAppPreference=InAppPreference.getInstance(this);
        inAppPreference.setImgUrl("");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_auto_wall) {
                    FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_Main, FirebaseEventManager.ATR_KEY_MENU, FirebaseEventManager.ATR_VALUE_POLICY);
                    startActivity(new Intent(MainActivity.this, AutoChangerActivity.class));
                }else if (id == R.id.nav_auto_Favorite) {
                    FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_Main, FirebaseEventManager.ATR_KEY_MENU, FirebaseEventManager.ATR_VALUE_FAVOURITE);
                    startActivity(new Intent(MainActivity.this, MyFavoriteActivity.class));
                }else if (id == R.id.nav_download) {
                    FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_Main, FirebaseEventManager.ATR_KEY_MENU, FirebaseEventManager.ATR_VALUE_DOWNLOADS);
                    Intent intent=new Intent(MainActivity.this, MyDownloadActivity.class);
                    startActivity(intent);
                }else if (id == R.id.nav_wall_not_working) {
                    FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_Main, FirebaseEventManager.ATR_KEY_MENU, FirebaseEventManager.ATR_VALUE_WALL_NOT_WORKING);

                    Intent intent=new Intent(MainActivity.this, PrivacyActivity.class);
                    intent.putExtra("isPrivacy",false);
                    startActivity(intent);
                    LoggerCustom.erorr("tag", "not working");

                }else if (id == R.id.nav_ratus) {
                    BlackApplication.getBlackApplication().isShareRate = true;
                    FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_Main, FirebaseEventManager.ATR_KEY_MENU, FirebaseEventManager.ATR_VALUE_RATE_US);
                    redirectToPlayStore();
                }else if (id == R.id.nav_share) {
                    BlackApplication.getBlackApplication().isShareRate = true;
                    FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_Main, FirebaseEventManager.ATR_KEY_MENU, FirebaseEventManager.ATR_VALUE_SHARE_APP);

//                activity.showLastFragment(activity.itemSelected);

                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");

                    i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                    try {
                        i.putExtra(
                                Intent.EXTRA_TEXT, SingletonControl.getInstance().getDataList().getLogic().getShareText());
                    } catch (Exception e) {
                        e.printStackTrace();
                        i.putExtra(
                                Intent.EXTRA_TEXT, "Get Blackzy - To Discover beautiful Black, Amoled and Dark Wallpapers. Download it from here.  https://bit.ly/478PoVI");
                    }

                    Intent chooser = Intent.createChooser(i, R.string.invite_frnd
                            + getString(R.string.app_name));
                    try {
                        startActivity(chooser);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if (id == R.id.nav_privacy_policy) {
                    Intent intent=new Intent(MainActivity.this, PrivacyActivity.class);
                    intent.putExtra("isPrivacy",true);
                    startActivity(intent);
                    return true;
                }else if (id == R.id.nav_support) {
                    BlackApplication.getBlackApplication().isShareRate = true;
                    FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_Main, FirebaseEventManager.ATR_KEY_MENU, FirebaseEventManager.ATR_VALUE_SUPPORT);
                    PackageInfo pInfo = null;
                    try {
                        pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        final String version = pInfo.versionName;
                        final Intent intent1 = new Intent(Intent.ACTION_SEND);
                        intent1.setType("plain/text");
                        intent1.setPackage("com.google.android.gm");
                        intent1.putExtra(Intent.EXTRA_EMAIL, new String[]{"brgv2003@gmail.com"});
                        String ids = getResources().getString(R.string.app_name) + "\nSupport ID: " + InAppPreference.getInstance(MainActivity.this).getUserId()
                                + "\nDevice: " + AppUtility.getDeviceTextName() + "\nVersion: " + version;
                        String user_id = ids + "\n\nDescribe your problem:";
                        intent1.putExtra(Intent.EXTRA_TEXT, "" + user_id);
                        startActivity(intent1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LoggerCustom.erorr("tag", e.getMessage());
                    }
                    LoggerCustom.erorr("tag", "support");
                }
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });

        setupViewPager(binding.viewpager);
        binding.tabs.setupWithViewPager(binding.viewpager);
        setupTabIcons();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getCurrentToken();
            }
        },3000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                grantNotificationPermission();
            }
        },1000);
    }
    private int backPressCount = 0;
    private boolean isDestroyFromBakcPress;
    @Override
    public void onBackPressed() {
        LoggerCustom.erorr("tag", "onBack : " + backPressCount);

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else if (binding.tabs.getSelectedTabPosition() == 0) {
            if (backPressCount == 0) {
                Toast.makeText(this, getString(R.string.press_again), Toast.LENGTH_SHORT).show();
                backPressCount = 1;
            } else {
                isDestroyFromBakcPress=true;
                finish();
                return;
            }
        } else {
            showLastFragment(0);
            return;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backPressCount = 0;
            }
        }, 2000);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.action_search){
            Intent intent=new Intent(MainActivity.this, WallpaperSearchActivity.class);
            startActivity(intent);
            return true;
        }
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupTabIcons() {
        binding.tabs.getTabAt(0).setText(getString(R.string.menu_home));
        binding.tabs.getTabAt(1).setText(getString(R.string.menu_live));
        binding.tabs.getTabAt(2).setText(getString(R.string.menu_double));
        binding.tabs.getTabAt(3).setText(getString(R.string.menu_category));
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPager.setOffscreenPageLimit(4);

        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        FeatureFragment featureFragment = null;
        HomeFragment liveWallFragment = null;
        DoubleWallpaperFragment doubleFragment = null;
        CategoryFragment categoryFragment = null;

        Bundle b = new Bundle();

        if (featureFragment == null) {
            b = new Bundle();
            featureFragment = new FeatureFragment();
            b.putSerializable("wallpaperType", WallpaperType.HOME_TYPE);
            featureFragment.setArguments(b);
            pagerAdapter.addFragment(featureFragment, "Home");

        }

        if (liveWallFragment == null) {
            liveWallFragment = new HomeFragment();
            b = new Bundle();
            b.putSerializable("wallpaperType", WallpaperType.LIVE_TYPE);
            liveWallFragment.setArguments(b);
            pagerAdapter.addFragment(liveWallFragment, "Live Wallpaper");
        }

        if (doubleFragment == null) {
            doubleFragment = new DoubleWallpaperFragment();
            b = new Bundle();
            doubleFragment.setArguments(b);
            pagerAdapter.addFragment(doubleFragment, "Double Wallpaper");
        }

        if (categoryFragment == null) {
            categoryFragment = new CategoryFragment();
            b = new Bundle();
            categoryFragment.setArguments(b);
            pagerAdapter.addFragment(categoryFragment, "Category");
        }

        viewPager.setAdapter(pagerAdapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

    }

    private void redirectToPlayStore(){
        final String appPackageName = getPackageName();
        BlackApplication.getBlackApplication().isShareRate = true;
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            LoggerCustom.erorr("rating error", e.getMessage());
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void ChangeFragment(int id) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                spaceNavigationView.changeCurrentItem(position);
                binding.viewpager.setCurrentItem(id, true);
//                spaceNavigationView.setNextFocusUpId(3);
            }
        }, 100);
    }

    public void showLastFragment(int i) {
        binding.viewpager.setCurrentItem(i, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void getCurrentToken(){
        try {
            if(inAppPreference!=null && inAppPreference.isFCMTokenSend())
                return;
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<String> task) {
                            try {
                                if (!task.isSuccessful()) {
                                    Log.w("MainActivity", "Fetching FCM registration token failed");
                                    return;
                                }

                                // Get new FCM registration token
                                String token = task.getResult();
                                Log.e("MainActivity getToken", "token:"+token);
                                inAppPreference.setFCM(token);
                                String user_id = inAppPreference.getUserId();
                                RetroApiRequest.updateFCMTokenRetro(getApplicationContext(), user_id, token, new RetroCallbacks() {
                                    @Override
                                    public void onDataSuccess(IModel result) throws JSONException {
                                    }

                                    @Override
                                    public void onDataError(String result) throws Exception {
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final int REQUEST_PERMISSIONS_CODE = 1112;
    private void grantNotificationPermission(){
        if(isFinishing())
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[] {android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_PERMISSIONS_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LoggerCustom.erorr("Permission", "onRequestPermissionsResult:");
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (grantResults != null && grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_PERMISSION, "Notification", "Granted");
            } else if (grantResults != null && grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                FirebaseEventManager.sendEventFirebase(FirebaseEventManager.LBL_PERMISSION, "Notification", "Denied");
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)){
                    LoggerCustom.erorr("Permission", "shouldShowRequestPermissionRationale:");
                    AppUtility.showDialogOK(this,"Permission required","Allow",getResources().getString(R.string.allow_per_guide_notification), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    grantNotificationPermission();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialogInterface.dismiss();
                                    break;
                            }
                        }
                    });
                }else{
                    try {
                        if (SingletonControl.getInstance().getDataList().getLogic().is_notication_ask_disable()) {
                            AppUtility.showDialogOK(this,"Permission Disabled","Go to settings",getResources().getString(R.string.allow_per_guide_notification1), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                                            intent.setData(uri);
                                            startActivity(intent);
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            // proceed with logic by disabling the related features or quit the app.
                                            dialogInterface.dismiss();
                                            break;
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    LoggerCustom.erorr("Permission", "shouldShowRequestPermissionRationale: else");
                }
            }
        }
    }
}