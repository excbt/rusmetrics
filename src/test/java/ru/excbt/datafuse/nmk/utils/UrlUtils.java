package ru.excbt.datafuse.nmk.utils;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kovtonyk on 07.03.2017.
 */
public class UrlUtils {

    /*

     */
    public static String apiSubscrUrl(String url) {
        checkNotNull(url);
        return "/api/subscr" + url;
    }

    /*

     */
    public static String apiSubscrUrl(String url, Object... args) {
        checkNotNull(url);
        return "/api/subscr" + String.format(url, args);
    }

    /*

     */
    public static String apiSubscrUrlTemplate(String url, Object... args) {
        checkNotNull(url);
        return "/api/subscr" + String.format(url, args);
    }

    /*

     */
    @Deprecated
    public static String apiSubscrUrl(String url, Long id) {
        checkNotNull(url);
        checkNotNull(id);
        return stringBuilderUtil("/api/subscr", url, "/", id.toString());
    }
    /*

     */
    public static String apiSubscrUrl(String url, Integer id) {
        checkNotNull(url);
        checkNotNull(id);
        return stringBuilderUtil("/api/subscr", url, "/", id.toString());
    }

    /*

     */
    public static String apiRmaUrl(String url) {
        checkNotNull(url);
        return "/api/rma" + url;
    }

    /*

     */
    public static String apiRmaUrl(String url, Object... args) {
        checkNotNull(url);
        return "/api/rma" + String.format(url, args);
    }

    /*

     */
    public static String apiRmaUrlTemplate(String url, Object... args) {
        checkNotNull(url);
        return "/api/rma" + String.format(url, args);
    }

    /*

     */
    @Deprecated
    public static String apiRmaUrl(String url, Long id) {
        checkNotNull(url);
        checkNotNull(id);
        return stringBuilderUtil("/api/rma", url, "/", id.toString());
    }

    /*

     */
    public static String apiRmaUrl(String url, Integer id) {
        checkNotNull(url);
        checkNotNull(id);
        return stringBuilderUtil("/api/rma", url, "/", id.toString());
    }

    /*

     */
    public static String stringBuilderUtil(String... strings) {
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            sb.append(s);
        }
        return sb.toString();
    }

}
