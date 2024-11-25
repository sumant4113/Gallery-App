package com.example.galleryapp.main;

import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.example.galleryapp.R;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class InfoUtil {

    public static class InfoItem {
        private String type, value;
        private int iconRes;

        public InfoItem(String type, String value) {
            this.type = type;
            this.value = value;
        }

        public InfoItem setIconRes(int iconRes) {
            this.iconRes = iconRes;
            return this;
        }

        public String getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        public int getIconRes() {
            return iconRes;
        }
    }

    // Retrieve File Name
    public static String retrieveFileName(Context context, Uri uri) {
        try {
            Cursor cursor = context.getContentResolver().query(uri,
                    new String[]{OpenableColumns.DISPLAY_NAME},
                    null, null, null);
            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                cursor.moveToFirst();
                if (!cursor.isAfterLast()) {
                    String filename = cursor.getString(nameIndex);
                    cursor.close();
                    return filename;
                }
            }
        } catch (SecurityException ignored) {
        }
        return "Unknown";
    }

    public static InfoItem retrieveFileSize(Context context, Uri uri) {
        long size = 0;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (sizeIndex != -1) {
                    size = cursor.getLong(sizeIndex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // Fallback: Check file size directly if ContentResolver fails
        if (size <= 0) {
            try {
                File file = new File(uri.getPath());
                if (file.exists()) {
                    size = file.length();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new InfoItem(context.getString(R.string.info_size), formatFileSize(size));
    }
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return String.format(Locale.getDefault(), "%.2f B", (float) size);
        } else if (size < 1024 * 1024) {
            return String.format(Locale.getDefault(), "%.2f KB", (float) size / 1024);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format(Locale.getDefault(), "%.2f MB", (float) size / (1024 * 1024));
        } else {
            return String.format(Locale.getDefault(), "%.2f GB", (float) size / (1024 * 1024 * 1024));
        }
    }

    // Retrieve Dimensions
   /* public static InfoItem retrieveDimensions(Context context, ExifInterface exif, AlbumItem albumItem) {
        if (exif != null) {
            String height = String.valueOf(exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0));
            String width = String.valueOf(exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0));
            return new InfoItem(context.getString(R.string.info_dimensions), width + " x " + height);
        }
        // Fallback if EXIF not available
        int[] dimensions = albumItem.getImageDimens(context);
        return new InfoItem(context.getString(R.string.info_dimensions), dimensions[0] + " x " + dimensions[1]);
    }

    // Retrieve Date
    public static InfoItem retrieveFormattedDate(Context context, ExifInterface exif, AlbumItem albumItem) {
        Locale locale = Locale.getDefault();
        if (exif != null) {
            String dateString = exif.getAttribute(ExifInterface.TAG_DATETIME);
            try {
                Date date = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", locale).parse(dateString);
                String formattedDate = new SimpleDateFormat("EEE d MMM yyyy HH:mm", locale).format(date);
                return new InfoItem(context.getString(R.string.info_date), formattedDate);
            } catch (ParseException ignored) {
            }
        }
        String formattedDate = new SimpleDateFormat("EEE d MMM yyyy HH:mm", locale)
                .format(new Date(albumItem.getDate()));
        return new InfoItem(context.getString(R.string.info_date), formattedDate);
    }*/

    // Retrieve ISO
    public static InfoItem retrieveISO(Context context, ExifInterface exif) {
        int iso = exif.getAttributeInt(ExifInterface.TAG_ISO, 0);
        return new InfoItem(context.getString(R.string.info_iso), iso > 0 ? String.valueOf(iso) : "Unknown");
    }

    // Retrieve Location
    /*public static LocationItem retrieveLocation(Context context, ExifInterface exif) {
        if (exif != null) {
            double latitude = exif.getAttributeDouble(ExifInterface.TAG_GPS_LATITUDE, 0);
            double longitude = exif.getAttributeDouble(ExifInterface.TAG_GPS_LONGITUDE, 0);
            String locationString = latitude + "," + longitude;
            return new LocationItem(context.getString(R.string.info_location), locationString);
        }
        return new LocationItem(context.getString(R.string.info_location), "No Data");
    }*/

    // Retrieve Address
    public static Address retrieveAddress(Context context, double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                return addresses.get(0);
            }
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }
}