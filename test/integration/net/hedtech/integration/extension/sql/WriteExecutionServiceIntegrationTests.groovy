/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.integration.extension.sql

import net.hedtech.banner.testing.BaseIntegrationTestCase
import net.hedtech.integration.extension.ExtensionDefinition
import net.hedtech.integration.extension.ExtractedExtensionProperty
import net.hedtech.integration.extension.ExtractedExtensionPropertyGroup
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.text.SimpleDateFormat

/**
 * WriteExecutionService tests.
 */
class WriteExecutionServiceIntegrationTests  extends BaseIntegrationTestCase {

    def writeExecutionService

    @Before
    public void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
    }


    @After
    public void tearDown() {
        super.tearDown()
    }

    @Test
    void whenValidExpectResults() {
        //Get a GUID by looking at GORGUID and grabbing one (support for every developers GUIDs)
        def maritalStatusGuidList = []
        def guidQuery = "select gorguid_guid from gorguid where gorguid_ldm_name = 'marital-status' and gorguid_domain_key = 'S'"
        def sqlQuery = sessionFactory.currentSession.createSQLQuery(guidQuery)
        def guidResults = sqlQuery.list()
        assertEquals 1, guidResults.size()
        guidResults.each { row ->
            maritalStatusGuidList.add(row)
        }

        def writeSql =
            """begin
                 if (:HTTP_METHOD = 'PUT') then
                     update stvmrtl
                        set stvmrtl_fa_conv_code = :STVMRTL_FA_CONV_CODE,
                            stvmrtl_edi_equiv = :STVMRTL_EDI_EQUIV,
                            stvmrtl_version = :STVMRTL_VERSION,
                            stvmrtl_activity_date = nvl(nvl(:STVMRTL_ACTIVITY_DATE,:STVMRTL_ACTIVITY_TIMESTAMP),sysdate)
                      where stvmrtl_surrogate_id = (select gorguid_domain_surrogate_id
                                                      from gorguid
                                                     where gorguid_ldm_name = 'marital-status'
                                                       and gorguid_guid = :GUID);
                  end if;                       
               end;"""


        ExtractedExtensionPropertyGroup extractedExtensionPropertyGroup = new ExtractedExtensionPropertyGroup()

        ExtractedExtensionProperty faExtractedExtensionProperty = new ExtractedExtensionProperty()
        ExtractedExtensionProperty ediExtractedExtensionProperty = new ExtractedExtensionProperty()
        ExtractedExtensionProperty numberExtractedExtensionProperty = new ExtractedExtensionProperty()
        ExtractedExtensionProperty dateExtractedExtensionProperty = new ExtractedExtensionProperty()
        ExtractedExtensionProperty timestampExtractedExtensionProperty = new ExtractedExtensionProperty()


        ExtensionDefinition faExtensionDefinition = new ExtensionDefinition()
        ExtensionDefinition ediExtensionDefinition = new ExtensionDefinition()
        ExtensionDefinition numberExtensionDefinition = new ExtensionDefinition()
        ExtensionDefinition dateExtensionDefinition = new ExtensionDefinition()
        ExtensionDefinition timestampExtensionDefinition = new ExtensionDefinition()

        faExtensionDefinition.columnName = "STVMRTL_FA_CONV_CODE"
        faExtensionDefinition.jsonPropertyType ="S"
        faExtractedExtensionProperty.value = "A"

        ediExtensionDefinition.columnName = "STVMRTL_EDI_EQUIV"
        ediExtensionDefinition.jsonPropertyType ="S"
        ediExtractedExtensionProperty.value = "B"

        numberExtensionDefinition.columnName = "STVMRTL_VERSION"
        numberExtensionDefinition.jsonPropertyType ="N"
        numberExtractedExtensionProperty.value = 999999

        dateExtensionDefinition.columnName = "STVMRTL_ACTIVITY_DATE"
        dateExtensionDefinition.jsonPropertyType ="D"
        dateExtractedExtensionProperty.value = new SimpleDateFormat("yyyy-MM-dd").parse("2017-06-25")

        timestampExtensionDefinition.columnName = "STVMRTL_ACTIVITY_TIMESTAMP"
        timestampExtensionDefinition.jsonPropertyType ="T"
        timestampExtractedExtensionProperty.value = new SimpleDateFormat("yyyy-MM-dd").parse("2016-04-29")

        faExtractedExtensionProperty.extensionDefinition=faExtensionDefinition
        ediExtractedExtensionProperty.extensionDefinition=ediExtensionDefinition
        numberExtractedExtensionProperty.extensionDefinition=numberExtensionDefinition
        dateExtractedExtensionProperty.extensionDefinition=dateExtensionDefinition
        timestampExtractedExtensionProperty.extensionDefinition=timestampExtensionDefinition

        extractedExtensionPropertyGroup.extractedExtensionPropertyList = []
        extractedExtensionPropertyGroup.extractedExtensionPropertyList.add(faExtractedExtensionProperty)
        extractedExtensionPropertyGroup.extractedExtensionPropertyList.add(ediExtractedExtensionProperty)
        extractedExtensionPropertyGroup.extractedExtensionPropertyList.add(numberExtractedExtensionProperty)
        extractedExtensionPropertyGroup.extractedExtensionPropertyList.add(dateExtractedExtensionProperty)
        extractedExtensionPropertyGroup.extractedExtensionPropertyList.add(timestampExtractedExtensionProperty)


        writeExecutionService.execute(writeSql, maritalStatusGuidList[0], "PUT", extractedExtensionPropertyGroup)

        def verifyQuery = "select stvmrtl_fa_conv_code, stvmrtl_edi_equiv, stvmrtl_version, stvmrtl_activity_date from stvmrtl where stvmrtl_code = 'S'"
        sqlQuery = sessionFactory.currentSession.createSQLQuery(verifyQuery)
        def verifyResults = sqlQuery.list()
        assertEquals 1, verifyResults.size()
        verifyResults.each { row ->
            assertEquals 'A', row[0]
            assertEquals 'B', row[1]
            assertEquals 999999, row[2].toInteger()
            assertEquals "2017-06-25", new SimpleDateFormat("yyyy-MM-dd").format(row[3])
        }
    }

    @Test
    void whenUnspecifiedValues() {
        //Get a GUID by looking at GORGUID and grabbing one (support for every developers GUIDs)
        def maritalStatusGuidList = []
        def guidQuery = "select gorguid_guid from gorguid where gorguid_ldm_name = 'marital-status' and gorguid_domain_key = 'S'"
        def sqlQuery = sessionFactory.currentSession.createSQLQuery(guidQuery)
        def guidResults = sqlQuery.list()
        assertEquals 1, guidResults.size()
        guidResults.each { row ->
            maritalStatusGuidList.add(row)
        }

        def writeSql =
                """begin
                    if (:HTTP_METHOD = 'PUT') then
                         if :GUID != 'test' then
                           raise_application_error (-20001,'Missing GUID parameter');
                         end if;
                         if :UNSPECIFIED_STRING != dml_common.unspecified_string then
                           raise_application_error (-20001,'Expected unspecified string '||dml_common.unspecified_string||', but was '||:UNSPECIFIED_STRING);
                         end if;
                         if :UNSPECIFIED_NUMBER != dml_common.unspecified_number then
                           raise_application_error (-20001,'Expected unspecified number '||dml_common.unspecified_number||', but was '||:UNSPECIFIED_NUMBER);
                         end if;
                         if :UNSPECIFIED_DATE != dml_common.unspecified_date then
                           raise_application_error (-20001,'Expected unspecified date '||dml_common.unspecified_date||', but was '||:UNSPECIFIED_DATE);
                         end if;
                         if :UNSPECIFIED_TIMESTAMP != dml_common.unspecified_date then
                           raise_application_error (-20001,'Expected unspecified timestamp '||dml_common.unspecified_date||', but was '||:UNSPECIFIED_TIMESTAMP);
                         end if;
                     end if;
               end;"""


        ExtractedExtensionPropertyGroup extractedExtensionPropertyGroup = new ExtractedExtensionPropertyGroup()

        ExtractedExtensionProperty stringExtractedExtensionProperty = new ExtractedExtensionProperty()
        ExtractedExtensionProperty numberExtractedExtensionProperty = new ExtractedExtensionProperty()
        ExtractedExtensionProperty dateExtractedExtensionProperty = new ExtractedExtensionProperty()
        ExtractedExtensionProperty timestampExtractedExtensionProperty = new ExtractedExtensionProperty()

        ExtensionDefinition stringExtensionDefinition = new ExtensionDefinition()
        ExtensionDefinition numberExtensionDefinition = new ExtensionDefinition()
        ExtensionDefinition dateExtensionDefinition = new ExtensionDefinition()
        ExtensionDefinition timestampExtensionDefinition = new ExtensionDefinition()

        stringExtensionDefinition.columnName = "UNSPECIFIED_STRING"
        stringExtensionDefinition.jsonPropertyType ="S"
        numberExtensionDefinition.columnName = "UNSPECIFIED_NUMBER"
        numberExtensionDefinition.jsonPropertyType = "N"
        dateExtensionDefinition.columnName = "UNSPECIFIED_DATE"
        dateExtensionDefinition.jsonPropertyType = "D"
        timestampExtensionDefinition.columnName = "UNSPECIFIED_TIMESTAMP"
        timestampExtensionDefinition.jsonPropertyType = "T"
        stringExtractedExtensionProperty.extensionDefinition=stringExtensionDefinition
        numberExtractedExtensionProperty.extensionDefinition=numberExtensionDefinition
        dateExtractedExtensionProperty.extensionDefinition=dateExtensionDefinition
        timestampExtractedExtensionProperty.extensionDefinition=timestampExtensionDefinition

        stringExtractedExtensionProperty.valueWasMissing = true
        numberExtractedExtensionProperty.valueWasMissing = true
        dateExtractedExtensionProperty.valueWasMissing = true
        timestampExtractedExtensionProperty.valueWasMissing = true

        extractedExtensionPropertyGroup.extractedExtensionPropertyList = []
        extractedExtensionPropertyGroup.extractedExtensionPropertyList.add(stringExtractedExtensionProperty)
        extractedExtensionPropertyGroup.extractedExtensionPropertyList.add(numberExtractedExtensionProperty)
        extractedExtensionPropertyGroup.extractedExtensionPropertyList.add(dateExtractedExtensionProperty)
        extractedExtensionPropertyGroup.extractedExtensionPropertyList.add(timestampExtractedExtensionProperty)

        writeExecutionService.execute(writeSql, "test", "PUT", extractedExtensionPropertyGroup)
    }

}
