package com.bitwave.cowdash.utils;

public class LabelFormatter {

    private byte amountOfLineBreaks;

    public String getFormattedString(final byte INDEX_FOR_LINE_BREAK, String textToFormat) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0, interval = 0; i < textToFormat.length(); i++, interval++) {
            char letter = textToFormat.charAt(i);
            builder.append(letter);

            if (interval >= INDEX_FOR_LINE_BREAK) {
                if (letter != ' ') {
                    int lastIndex = builder.lastIndexOf(" ");
                    builder.setCharAt(lastIndex, '\n');
                    interval = INDEX_FOR_LINE_BREAK - lastIndex;
                } else {
                    builder.setCharAt(i, '\n');
                    interval = -1;
                }
                amountOfLineBreaks++;
            }
        }
        return builder.toString();
    }

    public byte getAmountOfLineBreaks() {
        return amountOfLineBreaks;
    }

}
