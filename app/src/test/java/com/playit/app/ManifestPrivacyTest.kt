package com.playit.app

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ManifestPrivacyTest {

    private val srcMainDir = if (File("src/main").exists()) {
        File("src/main")
    } else {
        File("app/src/main")
    }

    @Test
    fun backupIsDisabled() {
        val manifestFile = File(srcMainDir, "AndroidManifest.xml")
        assertTrue("Manifest file not found: ${manifestFile.absolutePath}", manifestFile.exists())

        val manifestText = manifestFile.readText()
        assertTrue("Manifest should contain android:allowBackup=\"false\"", manifestText.contains("android:allowBackup=\"false\""))
        assertTrue("Manifest should contain @xml/backup_rules", manifestText.contains("@xml/backup_rules"))
        assertTrue("Manifest should contain @xml/data_extraction_rules", manifestText.contains("@xml/data_extraction_rules"))

        val backupRulesFile = File(srcMainDir, "res/xml/backup_rules.xml")
        assertTrue("backup_rules.xml not found", backupRulesFile.exists())
        val backupRulesText = backupRulesFile.readText()
        assertTrue("backup_rules.xml should contain domain=\"database\"", backupRulesText.contains("domain=\"database\""))

        val dataExtractionRulesFile = File(srcMainDir, "res/xml/data_extraction_rules.xml")
        assertTrue("data_extraction_rules.xml not found", dataExtractionRulesFile.exists())
        val dataExtractionRulesText = dataExtractionRulesFile.readText()
        assertTrue("data_extraction_rules.xml should contain domain=\"database\"", dataExtractionRulesText.contains("domain=\"database\""))
    }
}
