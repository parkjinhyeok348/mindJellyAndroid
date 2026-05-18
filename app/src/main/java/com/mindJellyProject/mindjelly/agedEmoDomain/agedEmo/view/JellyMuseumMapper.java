package com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.view;

import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmoMuseumResDTO;

import java.util.ArrayList;
import java.util.List;

final class JellyMuseumMapper {
    private JellyMuseumMapper() {
    }

    static List<DisplayJelly> toDisplayRows(List<AgedEmoMuseumResDTO> agedJellies) {
        List<DisplayJelly> rows = new ArrayList<>();
        if (agedJellies == null) {
            return rows;
        }

        for (AgedEmoMuseumResDTO agedJelly : agedJellies) {
            rows.add(new DisplayJelly(
                    "완성 젤리 ID: " + agedJelly.getAgedEmoId(),
                    "조합 ID: " + agedJelly.getJellyCombId(),
                    "완성일: " + nullToDash(agedJelly.getCreateDate())
            ));
        }
        return rows;
    }

    private static String nullToDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    static final class DisplayJelly {
        final String title;
        final String combinationText;
        final String createdText;

        DisplayJelly(String title, String combinationText, String createdText) {
            this.title = title;
            this.combinationText = combinationText;
            this.createdText = createdText;
        }
    }
}
