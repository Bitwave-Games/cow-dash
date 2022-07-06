package com.bitwave.cowdash.utils;

import com.bitwave.cowdash.objects.item.Back;
import com.bitwave.cowdash.objects.item.Body;
import com.bitwave.cowdash.objects.item.Head;
import com.bitwave.cowdash.objects.item.Leg;
import com.bitwave.cowdash.objects.item.Mask;

public class ItemUtils {

    public static final String HEAD = "costume_head";
    public static final String BODY = "costume_body";
    public static final String LEG = "costume_leg";
    public static final String BACK = "costume_back";
    public static final String MASK = "costume_mask";

    public static Enum getItem(String itemType, byte itemIndex) {
        if (itemType != null) {
            if (itemType.equalsIgnoreCase(HEAD)) {
                return Head.getValue(itemIndex);
            } else if (itemType.equalsIgnoreCase(BODY)) {
                return Body.getValue(itemIndex);
            } else if (itemType.equalsIgnoreCase(LEG)) {
                return Leg.getValue(itemIndex);
            } else if (itemType.equalsIgnoreCase(MASK)) {
                return Mask.getValue(itemIndex);
            } else if (itemType.equalsIgnoreCase(BACK)) {
                return Back.getValue(itemIndex);
            }
        }
        return null;
    }

    public static boolean isItemUnlocked(String itemType, byte itemIndex) {
        if (itemType != null) {
            if (itemType.equalsIgnoreCase(HEAD)) {
                return Head.getValue(itemIndex).unlocked();
            } else if (itemType.equalsIgnoreCase(BODY)) {
                return Body.getValue(itemIndex).unlocked();
            } else if (itemType.equalsIgnoreCase(LEG)) {
                return Leg.getValue(itemIndex).unlocked();
            } else if (itemType.equalsIgnoreCase(MASK)) {
                return Mask.getValue(itemIndex).unlocked();
            } else if (itemType.equalsIgnoreCase(BACK)) {
                return Back.getValue(itemIndex).unlocked();
            }
        }
        return false;
    }

}