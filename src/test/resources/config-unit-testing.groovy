environments {
	development {
		
	}
	testConfigWithoutCustom {
		file {
			runtimePath = "src/test/resources/runtime/config-test-withoutcustom.groovy"
			customPath = ""
			cleanUnitTestingGenerated = false
		}
		datasource {
			user = "user"
			pw = "pw"
		}		
	}
	testConfigWithCustom {
		file {
			runtimePath = "src/test/resources/runtime/config-test-withcustom.groovy"
			customPath = "src/test/resources/custom/test-custom.groovy"
			cleanUnitTestingGenerated = false
		}
		datasource {
			user = "user"
			pw = "pw"
		}
	}
	testConfigWithCustomWithRuntimeChanges {
		file {
			runtimePath = "src/test/resources/runtime/config-test-withcustom-runtimechanges.groovy"
			customPath = "src/test/resources/custom/test-custom-runtimechanges.groovy"
			cleanUnitTestingGenerated = false
		}
		datasource {
			user = "user"
			pw = "pw"
		}
	}
	testConfigWithoutCustomWithRuntimeChanges {
		file {
			runtimePath = "src/test/resources/runtime/config-test-withoutcustom-runtimechanges.groovy"
			customPath = "src/test/resources/custom/test-withoutcustom-runtimechanges.groovy"
			cleanUnitTestingGenerated = false
		}
		datasource {
			user = "user"
			pw = "pw"
		}
	}
}