package kr.co.ethree.icare.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import kr.co.ethree.icare.R;

/**
 * Created by lee on 2016-01-11.
 */
public class Utils {

    public static int getAppVerstion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static int exifDegrees(int orientation) {
        int degrees = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degrees = 90;
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                degrees = 180;
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                degrees = 270;
                break;

            default:
                break;
        }
        return degrees;
    }

    public static Bitmap bitmapRotate(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.setRotate(degrees,
                    (float)bitmap.getWidth() / 2, (float)bitmap.getHeight() / 2);

            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                if (bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch (OutOfMemoryError e) {

            }
        }
        return bitmap;
    }

    public static byte[] readFile(String path) {
        byte[] bArData = null;
        try {
            FileInputStream oInputStream = new FileInputStream(path);
            int nCount = oInputStream.available();
            if(nCount > 0) {
                bArData = new byte[nCount];
                oInputStream.read(bArData);
            }

            if(oInputStream != null) {
                oInputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bArData;
    }

    public static byte[] read(String filePath) {
        byte[] data = null;

        InputStream in = null;
        try {
            in = new FileInputStream(filePath);
            data = new byte[in.available()];
            in.read(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffff0000;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) 4);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static int getIndexStep(int index) {
        int step = 0;
        if (index < 90) {
            step = 1;
        } else if (index > 89 && index < 103) {
            step = 2;
        } else if (index > 102) {
            step = 3;
        }
        return step;
    }

    public static int getIndexStep(String index) {
        if (index != null) {
            return getIndexStep(Integer.parseInt(index));
        } else {
            return 0;
        }
    }

    public static int getTemperStep(String temper) {
        double temp = Double.parseDouble(temper);
        int step = 0;
        if (temp < 21) {
            step = 1;
        } else if (temp > 20 && temp < 25) {
            step = 2;
        } else if (temp > 24) {
            step = 3;
        }
        return step;
    }

    public static int getHumStep(String hum) {
        double temp = Double.parseDouble(hum);
        int step = 0;
        if (temp < 40) {
            step = 1;
        } else if (temp >= 40 && temp < 51) {
            step = 2;
        } else if (temp >= 51) {
            step = 3;
        }
        return step;
    }

    public static int getUvStep(String uv) {
        double temp = Double.parseDouble(uv);
        int step = 0;
        if (temp <= 2) {
            step = 1;
        } else if (temp >= 3 && temp <= 5) {
            step = 2;
        } else if (temp >= 6 && temp <= 7) {
            step = 3;
        } else if (temp >= 8 && temp <= 10) {
            step = 4;
        } else if (temp >= 11) {
            step = 5;
        }
        return step;
    }

    public static int getHeatIndex(String temper, String hum) {
        double temperNum = 0;
        double humNum = 0;
        if (temper != null && hum != null) {
            temperNum = Double.parseDouble(temper);
            humNum = Double.parseDouble(hum);
        }

        double temperF = (temperNum * 1.8) + 32;
        ELog.e(null, "temperF : " + temperF);

        int index = 0;

        if (temperF < 80 && humNum < 40) {
            index = 80;
        } else if ((temperF >= 80 && temperF < 84) && humNum < 40) {
            index = 80;
        } else if ((temperF >= 80 && temperF < 84) && (humNum >= 40 && humNum < 45)) {
            index = 80;
        } else if ((temperF >= 80 && temperF < 84) && (humNum >= 45 && humNum < 50)) {
            index = 80;
        } else if ((temperF >= 80 && temperF < 84) && (humNum >= 50 && humNum < 55)) {
            index = 81;
        } else if ((temperF >= 80 && temperF < 84) && (humNum >= 55 && humNum < 60)) {
            index = 81;
        } else if ((temperF >= 80 && temperF < 84) && (humNum >= 60 && humNum < 65)) {
            index = 82;
        } else if ((temperF >= 80 && temperF < 84) && (humNum >= 65 && humNum < 70)) {
            index = 82;
        } else if ((temperF >= 80 && temperF < 84) && (humNum >= 70 && humNum < 75)) {
            index = 83;
        } else if ((temperF >= 80 && temperF < 84) && (humNum >= 75 && humNum < 80)) {
            index = 84;
        } else if ((temperF >= 80 && temperF < 84) && (humNum >= 80 && humNum < 85)) {
            index = 84;
        } else if ((temperF >= 80 && temperF < 84) && (humNum >= 85 && humNum < 90)) {
            index = 85;
        } else if ((temperF >= 80 && temperF < 84) && (humNum >= 90 && humNum < 95)) {
            index = 86;
        } else if ((temperF >= 80 && temperF < 84) && (humNum >= 95 && humNum < 100)) {
            index = 86;
        } else if ((temperF >= 80 && temperF < 84) && humNum >= 100) { // TODO: 1line
            index = 87;
        } else if ((temperF >= 84 && temperF < 90) && humNum < 40) {
            index = 83;
        } else if ((temperF >= 84 && temperF < 90) && (humNum >= 40 && humNum < 45)) {
            index = 83;
        } else if ((temperF >= 84 && temperF < 90) && (humNum >= 45 && humNum < 50)) {
            index = 84;
        } else if ((temperF >= 84 && temperF < 90) && (humNum >= 50 && humNum < 55)) {
            index = 85;
        } else if ((temperF >= 84 && temperF < 90) && (humNum >= 55 && humNum < 60)) {
            index = 86;
        } else if ((temperF >= 84 && temperF < 90) && (humNum >= 60 && humNum < 65)) {
            index = 88;
        } else if ((temperF >= 84 && temperF < 90) && (humNum >= 65 && humNum < 70)) {
            index = 89;
        } else if ((temperF >= 84 && temperF < 90) && (humNum >= 70 && humNum < 75)) {
            index = 90;
        } else if ((temperF >= 84 && temperF < 90) && (humNum >= 75 && humNum < 80)) {
            index = 92;
        } else if ((temperF >= 84 && temperF < 90) && (humNum >= 80 && humNum < 85)) {
            index = 94;
        } else if ((temperF >= 84 && temperF < 90) && (humNum >= 85 && humNum < 90)) {
            index = 96;
        } else if ((temperF >= 84 && temperF < 90) && (humNum >= 90 && humNum < 95)) {
            index = 98;
        } else if ((temperF >= 84 && temperF < 90) && (humNum >= 95 && humNum < 100)) {
            index = 100;
        } else if ((temperF >= 84 && temperF < 90) && humNum >= 100) { // TODO: 2line
            index = 103;
        } else if ((temperF >= 90 && temperF < 94) && humNum < 40) {
            index = 91;
        } else if ((temperF >= 90 && temperF < 94) && (humNum >= 40 && humNum < 45)) {
            index = 91;
        } else if ((temperF >= 90 && temperF < 94) && (humNum >= 45 && humNum < 50)) {
            index = 93;
        } else if ((temperF >= 90 && temperF < 94) && (humNum >= 50 && humNum < 55)) {
            index = 95;
        } else if ((temperF >= 90 && temperF < 94) && (humNum >= 55 && humNum < 60)) {
            index = 97;
        } else if ((temperF >= 90 && temperF < 94) && (humNum >= 60 && humNum < 65)) {
            index = 100;
        } else if ((temperF >= 90 && temperF < 94) && (humNum >= 65 && humNum < 70)) {
            index = 103;
        } else if ((temperF >= 90 && temperF < 94) && (humNum >= 70 && humNum < 75)) {
            index = 105;
        } else if ((temperF >= 90 && temperF < 94) && (humNum >= 75 && humNum < 80)) {
            index = 109;
        } else if ((temperF >= 90 && temperF < 94) && (humNum >= 80 && humNum < 85)) {
            index = 113;
        } else if ((temperF >= 90 && temperF < 94) && (humNum >= 85 && humNum < 90)) {
            index = 117;
        } else if ((temperF >= 90 && temperF < 94) && (humNum >= 90 && humNum < 95)) {
            index = 122;
        } else if ((temperF >= 90 && temperF < 94) && (humNum >= 95 && humNum < 100)) {
            index = 127;
        } else if ((temperF >= 90 && temperF < 94) && humNum >= 100) { // TODO: 3line
            index = 132;
        } else if ((temperF >= 94 && temperF < 100) && humNum < 40) {
            index = 97;
        } else if ((temperF >= 94 && temperF < 100) && (humNum >= 40 && humNum < 45)) {
            index = 97;
        } else if ((temperF >= 94 && temperF < 100) && (humNum >= 45 && humNum < 50)) {
            index = 100;
        } else if ((temperF >= 94 && temperF < 100) && (humNum >= 50 && humNum < 55)) {
            index = 103;
        } else if ((temperF >= 94 && temperF < 100) && (humNum >= 55 && humNum < 60)) {
            index = 106;
        } else if ((temperF >= 94 && temperF < 100) && (humNum >= 60 && humNum < 65)) {
            index = 110;
        } else if ((temperF >= 94 && temperF < 100) && (humNum >= 65 && humNum < 70)) {
            index = 114;
        } else if ((temperF >= 94 && temperF < 100) && (humNum >= 70 && humNum < 75)) {
            index = 119;
        } else if ((temperF >= 94 && temperF < 100) && (humNum >= 75 && humNum < 80)) {
            index = 124;
        } else if ((temperF >= 94 && temperF < 100) && (humNum >= 80 && humNum < 85)) {
            index = 129;
        } else if ((temperF >= 94 && temperF < 100) && (humNum >= 85 && humNum < 90)) {
            index = 135;
        } else if ((temperF >= 94 && temperF < 100) && (humNum >= 90 && humNum < 95)) {
            index = 135;
        } else if ((temperF >= 94 && temperF < 100) && (humNum >= 95 && humNum < 100)) {
            index = 135;
        } else if ((temperF >= 94 && temperF < 100) && humNum >= 100) { // TODO: 4line
            index = 135;
        } else if ((temperF >= 100 && temperF < 104) && humNum < 40) {
            index = 109;
        } else if ((temperF >= 100 && temperF < 104) && (humNum >= 40 && humNum < 45)) {
            index = 109;
        } else if ((temperF >= 100 && temperF < 104) && (humNum >= 45 && humNum < 50)) {
            index = 114;
        } else if ((temperF >= 100 && temperF < 104) && (humNum >= 50 && humNum < 55)) {
            index = 118;
        } else if ((temperF >= 100 && temperF < 104) && (humNum >= 55 && humNum < 60)) {
            index = 124;
        } else if ((temperF >= 100 && temperF < 104) && (humNum >= 60 && humNum < 65)) {
            index = 129;
        } else if ((temperF >= 100 && temperF < 104) && (humNum >= 65 && humNum < 70)) {
            index = 130;
        } else if ((temperF >= 100 && temperF < 104) && (humNum >= 70 && humNum < 75)) {
            index = 130;
        } else if ((temperF >= 100 && temperF < 104) && (humNum >= 75 && humNum < 80)) {
            index = 130;
        } else if ((temperF >= 100 && temperF < 104) && (humNum >= 80 && humNum < 85)) {
            index = 130;
        } else if ((temperF >= 100 && temperF < 104) && (humNum >= 85 && humNum < 90)) {
            index = 130;
        } else if ((temperF >= 100 && temperF < 104) && (humNum >= 90 && humNum < 95)) {
            index = 130;
        } else if ((temperF >= 100 && temperF < 104) && (humNum >= 95 && humNum < 100)) {
            index = 130;
        } else if ((temperF >= 100 && temperF < 104) && humNum >= 100) { // TODO: 5line
            index = 130;
        } else if (temperF >= 104 && humNum < 40) {
            index = 119;
        } else if (temperF >= 104 && (humNum >= 40 && humNum < 45)) {
            index = 124;
        } else if (temperF >= 104 && (humNum >= 45 && humNum < 50)) {
            index = 131;
        } else if (temperF >= 104 && (humNum >= 50 && humNum < 55)) {
            index = 137;
        } else if (temperF >= 104 && (humNum >= 55 && humNum < 60)) {
            index = 137;
        } else if (temperF >= 104 && (humNum >= 60 && humNum < 65)) {
            index = 137;
        } else if (temperF >= 104 && (humNum >= 65 && humNum < 70)) {
            index = 137;
        } else if (temperF >= 104 && (humNum >= 70 && humNum < 75)) {
            index = 137;
        } else if (temperF >= 104 && (humNum >= 75 && humNum < 80)) {
            index = 137;
        } else if (temperF >= 104 && (humNum >= 80 && humNum < 85)) {
            index = 137;
        } else if (temperF >= 104 && (humNum >= 85 && humNum < 90)) {
            index = 137;
        } else if (temperF >= 104 && (humNum >= 90 && humNum < 95)) {
            index = 137;
        } else if (temperF >= 104 && (humNum >= 95 && humNum < 100)) {
            index = 137;
        } else if (temperF >= 104 && humNum >= 100) { // TODO: 6line
            index = 137;
        }

        return index;
    }

    public static String getIndex(String temper, String hum) {
        int temp = getHeatIndex(temper, hum);
        String index = String.valueOf(temp);

        return index;
    }

    public static String getDateTime() {
        Date date = new Date();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dateTime = format.format(date);

        return dateTime;
    }

    public static Date StringToDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = null;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static SpannableStringBuilder monitorDate(String dateString) {
        Date date = StringToDate(dateString);
        SimpleDateFormat format = new SimpleDateFormat("MM.dd HH:mm:ss", Locale.getDefault());
        String time = format.format(date);

        SpannableStringBuilder sb = new SpannableStringBuilder(time);
        sb.setSpan(new StyleSpan(Typeface.BOLD), 0 , 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        return sb;
    }

    public static void sendSNS(Context context) {
        String text = "# 아이케어를 통해 내\n아이 면역력에 대한 예방정보를\n직접 확인해 보세요!\nhttp://www.icaremom.co.kr";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");
        context.startActivity(Intent.createChooser(intent, "아이케어 공유"));
    }

    public static void disConnectToast(Context context) {
        EToast.show(context, "아이케어 기기와 연결이 끊어졌습니다.", EToast.LENGTH_SHORT);
    }

    public static void heatIndexBad(Context context) {
        EToast.show(context, "아이지수가 나쁨 상태 입니다.", EToast.LENGTH_SHORT);
    }

}
