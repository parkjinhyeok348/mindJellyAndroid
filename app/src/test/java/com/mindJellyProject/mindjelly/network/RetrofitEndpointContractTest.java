package com.mindJellyProject.mindjelly.network;

import com.mindJellyProject.mindjelly.jellyDomain.jelly.retrofit.JellyService;
import com.mindJellyProject.mindjelly.jellyDomain.jellyCombination.retrofit.JellyCombService;

import org.junit.Test;

import java.lang.reflect.Method;

import retrofit2.http.GET;
import retrofit2.http.PATCH;

import static org.junit.Assert.assertEquals;

public class RetrofitEndpointContractTest {
    @Test
    public void startAgingUsesSameJellyRouteFamilyAsOtherJellyEndpoints() throws Exception {
        Method method = JellyService.class.getMethod(
                "startAging",
                Long.class,
                com.mindJellyProject.mindjelly.jellyDomain.jelly.model.JellyStartAgingReqDTO.class
        );

        PATCH patch = method.getAnnotation(PATCH.class);

        assertEquals("/jelly/{jellyId}", patch.value());
    }

    @Test
    public void jellyCombIdEndpointIsExplicitlyTracked() throws Exception {
        Method method = JellyCombService.class.getMethod("getJellyCombId", Long.class, Long.class);

        GET get = method.getAnnotation(GET.class);

        assertEquals("/jellyComb/jelly-comb-id/{firstEmo}/{secondEmo}", get.value());
    }
}
