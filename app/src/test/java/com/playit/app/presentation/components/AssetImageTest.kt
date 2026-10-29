package com.playit.app.presentation.components

import org.junit.Assert.assertEquals
import org.junit.Test

class AssetImageTest {
    @Test fun inSampleSize_512to120_is4() = assertEquals(4, calculateInSampleSize(512, 512, 120, 120))
    @Test fun inSampleSize_512to200_is2() = assertEquals(2, calculateInSampleSize(512, 512, 200, 200))
    @Test fun inSampleSize_512to512_is1() = assertEquals(1, calculateInSampleSize(512, 512, 512, 512))
    @Test fun inSampleSize_neverZero() = assertEquals(1, calculateInSampleSize(100, 100, 0, 0))
}
