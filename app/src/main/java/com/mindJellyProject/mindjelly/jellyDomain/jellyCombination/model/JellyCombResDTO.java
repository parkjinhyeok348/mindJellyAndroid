package com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.model
 * @description : 젤리 조합 정보를 가져오기 위한 JellyCombination Response DTO
 * @modification : 2025-01-03(Jinhyeok) 수정
 * @date : 2025-01-03
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     Jinhyeok        주석 생성
 */
public class JellyCombResDTO {
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

    public JellyCombResDTO(Long jellyCombId, Long firstEmo, Long secondEmo, Boolean isAwaken, String jellyIcon) {
        this.jellyCombId = jellyCombId;
        this.firstEmo = firstEmo;
        this.secondEmo = secondEmo;
        this.isAwaken = isAwaken;
        this.jellyIcon = jellyIcon;
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
}
