/*
 * Copyright (c) 2009 - 2016 groshev.net
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    This product includes software developed by the groshev.net.
 * 4. Neither the name of the groshev.net nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY groshev.net ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL groshev.net BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.groshev.rest.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * ISO 8601 date parsing utility.  Designed for parsing the ISO subset used in
 * Dublin Core, RSS 1.0, and Atom.
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton (burtonator)</a>
 * @version $Id: ISO8601DateParser.java,v 1.2 2005/06/03 20:25:29 snoopdave Exp $
 */
public class ISO8601DateParser {


    // 2004-06-14T19:GMT20:30Z
    // 2004-06-20T06:GMT22:01Z

    // http://www.cl.cam.ac.uk/~mgk25/iso-time.html
    //
    // http://www.intertwingly.net/wiki/pie/DateTime
    //
    // http://www.w3.org/TR/NOTE-datetime
    //
    // Different standards may need different levels of granularity in the date and
    // time, so this profile defines six levels. Standards that reference this
    // profile should specify one or more of these granularities. If a given
    // standard allows more than one granularity, it should specify the meaning of
    // the dates and times with reduced precision, for example, the result of
    // comparing two dates with different precisions.

    // The formats are as follows. Exactly the components shown here must be
    // present, with exactly this punctuation. Note that the "T" appears literally
    // in the string, to indicate the beginning of the time element, as specified in
    // ISO 8601.

    //    Year:
    //       YYYY (eg 1997)
    //    Year and month:
    //       YYYY-MM (eg 1997-07)
    //    Complete date:
    //       YYYY-MM-DD (eg 1997-07-16)
    //    Complete date plus hours and minutes:
    //       YYYY-MM-DDThh:mmTZD (eg 1997-07-16T19:20+01:00)
    //    Complete date plus hours, minutes and seconds:
    //       YYYY-MM-DDThh:mm:ssTZD (eg 1997-07-16T19:20:30+01:00)
    //    Complete date plus hours, minutes, seconds and a decimal fraction of a
    // second
    //       YYYY-MM-DDThh:mm:ss.sTZD (eg 1997-07-16T19:20:30.45+01:00)

    // where:

    public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";

    private ISO8601DateParser() {
        //dummy
    }

    /**
     * parse input string to get date object
     *
     * YYYY = four-digit year
     * MM   = two-digit month (01=January, etc.)
     * DD   = two-digit day of month (01 through 31)
     * hh   = two digits of hour (00 through 23) (am/pm NOT allowed)
     * mm   = two digits of minute (00 through 59)
     * ss   = two digits of second (00 through 59)
     * s    = one or more digits representing a decimal fraction of a second
     * TZD  = time zone designator (Z or +hh:mm or -hh:mm)
     * @param input input string
     * @return parsed date
     * @throws java.text.ParseException if there were errors
     */
    public static Date parse(String input) throws java.text.ParseException {

        //NOTE: SimpleDateFormat uses GMT[-+]hh:mm for the TZ which breaks
        //things a bit.  Before we go on we have to repair this.
        final SimpleDateFormat df = getDateFormat();

        StringBuilder parseString = new StringBuilder();
        //this is zero time so we need to add that TZ indicator for
        if (input.endsWith("Z")) {
            parseString.append(input.substring(0, input.length() - 1));
            parseString.append("GMT-00:00");
        } else {
            final int inset = 6;
            parseString.append(input.substring(0, input.length() - inset));
            parseString.append("GMT");
            parseString.append(input.substring(input.length() - inset, input.length()));
        }
        return df.parse(parseString.toString());

    }

    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat(getDatePattern());
    }

    public static String getDatePattern() {
        return DATE_PATTERN;
    }

    /**
     *  Приведение даты к строке (TZ=UTC)
     *
     * @param date дата
     * @return отформатированная строка
     */
    public static String toString(Object date) {
        final SimpleDateFormat df = getDateFormat();
        final TimeZone tz = TimeZone.getTimeZone("UTC");
        df.setTimeZone(tz);
        String result = df.format(date);
        result = result.replaceAll("UTC", "+00:00");

        return result;
    }

    /**
     *  Приведение даты к строке (TZ=UTC)
     *
     * @param date дата
     * @return отформатированная строка
     */
    public static String toDateString(final Date date) {
        final SimpleDateFormat df = getDateFormat();
        final TimeZone tz = TimeZone.getTimeZone("UTC");
        df.setTimeZone(tz);
        return df.format(date);
    }

    /**
     * Приведение даты к строке (TZ=UTC)
     *
     * @param date дата
     * @return отформатированная строка
     */
    public static String toLocalDateString(final Date date) {
        SimpleDateFormat df = getDateFormat();
        return df.format(date);
    }
}
