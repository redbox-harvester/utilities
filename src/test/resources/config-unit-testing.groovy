environments {
	development {
		
	}
	testConfigWithoutCustom {
		file {
			runtimePath = "src/test/resources/generated/config-test-withoutcustom.groovy"
			customPath = ""
			cleanUnitTestingGenerated = false
		}
		datasource {
			user = "user"
			pw = "pw"
		}
		cleanGenerated = false
	}
	testConfigWithCustom {
		file {
			runtimePath = "src/test/resources/generated/config-test-withcustom.groovy"
			customPath = "src/test/resources/custom/test-custom.groovy"
			cleanUnitTestingGenerated = false
		}
		datasource {
			user = "user"
			pw = "pw"
		}
	}
	
}