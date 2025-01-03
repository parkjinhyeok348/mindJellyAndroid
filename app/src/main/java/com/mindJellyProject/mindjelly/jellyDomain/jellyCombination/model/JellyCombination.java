package com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.model;

import com.google.gson.annotations.SerializedName;
import com.mindJellyProject.mindjelly.agedEmoDomain.agedEmo.model.AgedEmo;
import com.mindJellyProject.mindjelly.jellyDomain.jelly.model.Jelly;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.model
 * @description : JellyCombination Model
 * @modification : 2025-01-03(Jinhyeok) 수정
 * @date : 2025-01-03
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 */
public class JellyCombination {
    @SerializedName("jellyCombId")
    private Long jellyCombId; // 젤리 조합 ID

    @SerializedName("firstEmo")
    private Long firstEmo; // 첫 번째 감정 ID

    @SerializedName("secondEmo")
    private Long secondEmo; // 두 번째 감정 ID

    @SerializedName("isAwaken")
    private Boolean isAwaken; // 깨달음 여부

    @SerializedName("jellyIcon")
    private String jellyIcon; // 아이콘 영문 이름

    @SerializedName("jellyList")
    private List<Jelly> jellyList = new ArrayList<>(); // 젤리 조합에 속한 젤리 리스트

    @SerializedName("agedEmoList")
    private List<AgedEmo> agedEmoList = new ArrayList<>(); // 젤리 조합에 속한 숙성된 감정 리스트

    public JellyCombination(Long jellyCombId, Long firstEmo, Long secondEmo, Boolean isAwaken, String jellyIcon, List<Jelly> jellyList, List<AgedEmo> agedEmoList) {
        this.jellyCombId = jellyCombId;
        this.firstEmo = firstEmo;
        this.secondEmo = secondEmo;
        this.isAwaken = isAwaken;
        this.jellyIcon = jellyIcon;
        this.jellyList = jellyList;
        this.agedEmoList = agedEmoList;
    }

    public Long getJellyCombId() {
        return jellyCombId;
    }

    public void setJellyCombId(Long jellyCombId) {
        this.jellyCombId = jellyCombId;
    }

    public Long getFirstEmo() {
        return firstEmo;
    }

    public void setFirstEmo(Long firstEmo) {
        this.firstEmo = firstEmo;
    }

    public Long getSecondEmo() {
        return secondEmo;
    }

    public void setSecondEmo(Long secondEmo) {
        this.secondEmo = secondEmo;
    }

    public Boolean getAwaken() {
        return isAwaken;
    }

    public void setAwaken(Boolean awaken) {
        isAwaken = awaken;
    }

    public String getJellyIcon() {
        return jellyIcon;
    }

    public void setJellyIcon(String jellyIcon) {
        this.jellyIcon = jellyIcon;
    }

    public List<Jelly> getJellyList() {
        return jellyList;
    }

    public void setJellyList(List<Jelly> jellyList) {
        this.jellyList = jellyList;
    }

    public List<AgedEmo> getAgedEmoList() {
        return agedEmoList;
    }

    public void setAgedEmoList(List<AgedEmo> agedEmoList) {
        this.agedEmoList = agedEmoList;
    }
}
