package ru.excbt.datafuse.nmk.passdoc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by kovtonyk on 29.06.2017.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PDAnchor {

    private String key;
    private String caption;

    public static PDAnchor newAnchor(String key, String caption) {
        Preconditions.checkArgument(key != null && !key.isEmpty());
        Preconditions.checkArgument(caption != null && !caption.isEmpty());
        PDAnchor anchor = new PDAnchor();
        anchor.key = key;
        anchor.caption = caption;
        return anchor;
    }

}
