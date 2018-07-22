package com.sam.music.player.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.sam.music.player.RHSApp;
import com.sam.music.player.R;

/**
 * Created by i301487 on 12/30/15.
 */
public class SecurityUtils {

    private static int mNumber(int nParam) {
        int j = 0;
        for (int i = 1; i < 5; i++, nParam++) {
            nParam = j;
            j += i;
            nParam = i;
        }
        return j;
    }

    public static String getFixedSignature() {

        String strMe = "e499ee9f7d308201ff30820168a003020102020453fd957c300d06092a864886f70d01010505003043310b300906035504061302434e3111300f060355040813085368616e676861693111300f060355040713085368616e67686169310e300c0603550403130573616d77753020170d3134303832373038323332345a180f32313134303830333038323332345a3043310b300906035504061302434e3111300f060355040813085368616e676861693111300f060355040713085368616e67686169310e300c0603550403130573616d777530819f300d06092a864886f70d010101050003818d0030818902818100dc65d5edbb54de29ab3c1a6649cc94acf4b2ca18c8e77a2fa63f8e0131c63c5bcced9093f572c34aa55cda4a5b5a7e3d2c3746f8b9208743686eac0494d570cae351ecb2554b48be9ad021e3beb9e5ac2687a0c53bdec41be0131cfbc1f5bd42f8107ad18daa4c13023cb7dd64862c8058097d18f14c8bcaf5c17dfbcc6283450203010001300d06092a864886f70d010105050003818100ae3a1b9d78083e53b1b0127f8506b38172b7deccf2d5afa00a70643e5328427a4626db25caef12db373c1d95347698571d208f7cf843fe4da1431b8746ad86d154f0fe082e5dc816b23cfcc4df9aa05cae5423c4c04cb280339e7ab549c988dec787137e3a47b0de04c9e543337642cc03929c129eba352fd75964";

        String strPre = strMe.substring(0, mNumber(getHashValue()));
        String strAfter = strMe.substring(mNumber(9357), strMe.length());
        String strEntire = strAfter + strPre;

        return strEntire;
    }

    public static String getAppSignature() {
        String strPkgName = RHSApp.getAppContext().getPackageName();

        PackageManager manager = RHSApp.getAppContext().getPackageManager();
        PackageInfo appInfo;
        try {
            appInfo = manager.getPackageInfo(strPkgName,
                    PackageManager.GET_SIGNATURES);
            return appInfo.signatures[0].toCharsString();
        } catch (PackageManager.NameNotFoundException e) {
            //Do nothing
        }

        return null;
    }

    private static int getHashValue() {
        int n = 2 << 10;
        String tempValue = RHSApp.getAppContext().getString(R.string.app_name);

        for (int k = 3 << 7; k < 1024; k++) {
            n += k + tempValue.hashCode();
        }

        return n;
    }

    public static String getHashCode() {
        return String.valueOf(Math.abs(getHashValue()+ getFixedSignature().hashCode()));
    }

}
