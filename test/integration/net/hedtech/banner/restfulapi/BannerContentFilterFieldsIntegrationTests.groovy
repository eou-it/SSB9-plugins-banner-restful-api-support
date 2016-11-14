/*******************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.restfulapi

import net.hedtech.banner.testing.BaseIntegrationTestCase

import net.hedtech.restfulapi.ContentFilterFields

import groovy.sql.Sql

import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Test class for BannerContentFilterFields
 */
class BannerContentFilterFieldsIntegrationTests extends BaseIntegrationTestCase {

    ContentFilterFields restContentFilterFields


    @Before
    public void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
        teardownTestData()
        restContentFilterFields = new BannerContentFilterFields()
        restContentFilterFields.sessionFactory = sessionFactory
    }


    @After
    public void tearDown() {
        teardownTestData()
        super.tearDown()
    }


    @Test
    void testResourceNameNotFound() {
        assertEquals 0, verifyCount()

        // test resource
        List fieldPatterns = restContentFilterFields.retrieveFieldPatterns("my-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 0, fieldPatterns.size()
    }


    @Test
    void testWithOneFieldPattern() {
        def testData = [
            [resourceName: 'my-resource', fieldPattern: 'name', seqno: 1, displayInd: 'N', userPattern: '*']
        ]
        createTestData(testData)
        assertEquals 1, verifyCount()

        // test resource
        List fieldPatterns = restContentFilterFields.retrieveFieldPatterns("my-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 1, fieldPatterns.size()
        assertEquals "name", fieldPatterns.get(0)

        // test another resource name not found
        fieldPatterns = restContentFilterFields.retrieveFieldPatterns("another-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 0, fieldPatterns.size()
    }


    @Test
    void testWithMultipleFieldPatternsSorted() {
        def testData = [
            [resourceName: 'my-resource', fieldPattern: 'name', seqno: 1, displayInd: 'N', userPattern: '*'],
            [resourceName: 'my-resource', fieldPattern: 'code', seqno: 1, displayInd: 'N', userPattern: '*'],
            [resourceName: 'my-resource', fieldPattern: 'desc', seqno: 1, displayInd: 'N', userPattern: '*']
        ]
        createTestData(testData)
        assertEquals 3, verifyCount()

        // test resource
        List fieldPatterns = restContentFilterFields.retrieveFieldPatterns("my-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 3, fieldPatterns.size()
        assertEquals "code", fieldPatterns.get(0)
        assertEquals "desc", fieldPatterns.get(1)
        assertEquals "name", fieldPatterns.get(2)

        // test another resource name not found
        fieldPatterns = restContentFilterFields.retrieveFieldPatterns("another-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 0, fieldPatterns.size()
    }


    @Test
    void testWithDuplicateFieldPatternsRemoved() {
        def testData = [
            [resourceName: 'my-resource', fieldPattern: 'name', seqno: 1, displayInd: 'N', userPattern: '*'],
            [resourceName: 'my-resource', fieldPattern: 'code', seqno: 1, displayInd: 'N', userPattern: '*'],
            [resourceName: 'my-resource', fieldPattern: 'name', seqno: 2, displayInd: 'N', userPattern: '*'],
            [resourceName: 'my-resource', fieldPattern: 'desc', seqno: 1, displayInd: 'N', userPattern: '*'],
            [resourceName: 'my-resource', fieldPattern: 'code', seqno: 2, displayInd: 'N', userPattern: '*']
        ]
        createTestData(testData)
        assertEquals 5, verifyCount()

        // test resource
        List fieldPatterns = restContentFilterFields.retrieveFieldPatterns("my-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 3, fieldPatterns.size()
        assertEquals "code", fieldPatterns.get(0)
        assertEquals "desc", fieldPatterns.get(1)
        assertEquals "name", fieldPatterns.get(2)

        // test another resource name not found
        fieldPatterns = restContentFilterFields.retrieveFieldPatterns("another-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 0, fieldPatterns.size()
    }


    @Test
    void testFieldPatternsByUser() {
        def testData = [
            [resourceName: 'my-resource', fieldPattern: 'name', seqno: 1, displayInd: 'N', userPattern: 'OTHER_USER'],
            [resourceName: 'my-resource', fieldPattern: 'code', seqno: 1, displayInd: 'N', userPattern: 'GRAILS_USER'],
            [resourceName: 'my-resource', fieldPattern: 'desc', seqno: 1, displayInd: 'N', userPattern: 'OTHER_USER']
        ]
        createTestData(testData)
        assertEquals 3, verifyCount()

        // test resource
        List fieldPatterns = restContentFilterFields.retrieveFieldPatterns("my-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 1, fieldPatterns.size()
        assertEquals "code", fieldPatterns.get(0)
    }


    @Test
    void testFieldPatternsByGroup() {
        // groups and users within those groups are created automatically
        // when referenced by the group irregardless of the field pattern
        // for which they are specified; the API_TEST1_FPBR group will contain
        // all 3 users which are correlated to all 3 field patterns
        def testData = [
            [resourceName: 'my-resource', fieldPattern: 'name', seqno: 1, displayInd: 'N', userPattern: 'OTHER_USER:API_TEST1_FPBR'],
            [resourceName: 'my-resource', fieldPattern: 'code', seqno: 1, displayInd: 'N', userPattern: 'GRAILS_USER:API_TEST1_FPBR'],
            [resourceName: 'my-resource', fieldPattern: 'desc', seqno: 1, displayInd: 'N', userPattern: 'ANOTHER_USER:API_TEST1_FPBR']
        ]
        createTestData(testData)
        assertEquals 3, verifyCount()

        // test resource
        List fieldPatterns = restContentFilterFields.retrieveFieldPatterns("my-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 3, fieldPatterns.size()
        assertEquals "code", fieldPatterns.get(0)
        assertEquals "desc", fieldPatterns.get(1)
        assertEquals "name", fieldPatterns.get(2)
    }


    @Test
    void testFieldPatternsByOrderedGroupCode() {
        // use the alphabetically first group if a person is referenced
        // by multiple groups for the same field pattern
        def testData = [
            [resourceName: 'my-resource', fieldPattern: 'name', seqno: 1, displayInd: 'Y', userPattern: 'GRAILS_USER:API_TEST2_FPBR'],
            [resourceName: 'my-resource', fieldPattern: 'name', seqno: 2, displayInd: 'N', userPattern: 'GRAILS_USER:API_TEST1_FPBR']
        ]
        createTestData(testData)
        assertEquals 2, verifyCount()

        // test resource
        List fieldPatterns = restContentFilterFields.retrieveFieldPatterns("my-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 1, fieldPatterns.size()
        assertEquals "name", fieldPatterns.get(0)

        // add another entry to cause field pattern to be removed
        testData.add(
            [resourceName: 'my-resource', fieldPattern: 'name', seqno: 3, displayInd: 'Y', userPattern: 'GRAILS_USER:API_TEST0_FPBR']
        )
        createTestData(testData)
        assertEquals 3, verifyCount()

        // test resource
        fieldPatterns = restContentFilterFields.retrieveFieldPatterns("my-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 0, fieldPatterns.size()
    }


    @Test
    void testFieldPatternsPrioritization() {
        def testData = [
            [resourceName: 'my-resource', fieldPattern: 'name', seqno: 1, displayInd: 'N', userPattern: '*'],
            [resourceName: 'my-resource', fieldPattern: 'code', seqno: 1, displayInd: 'N', userPattern: '*'],
            [resourceName: 'my-resource', fieldPattern: 'desc', seqno: 1, displayInd: 'N', userPattern: '*']
        ]
        createTestData(testData)
        assertEquals 3, verifyCount()

        // test resource
        List fieldPatterns = restContentFilterFields.retrieveFieldPatterns("my-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 3, fieldPatterns.size()
        assertEquals "code", fieldPatterns.get(0)
        assertEquals "desc", fieldPatterns.get(1)
        assertEquals "name", fieldPatterns.get(2)

        // add another entry to show groups can override all users
        testData.add(
                [resourceName: 'my-resource', fieldPattern: 'desc', seqno: 2, displayInd: 'Y', userPattern: 'GRAILS_USER:API_TEST0_FPBR']
        )
        createTestData(testData)
        assertEquals 4, verifyCount()

        // test resource
        fieldPatterns = restContentFilterFields.retrieveFieldPatterns("my-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 2, fieldPatterns.size()
        assertEquals "code", fieldPatterns.get(0)
        assertEquals "name", fieldPatterns.get(1)

        // add another entry to show individual user can override groups all users
        testData.add(
            [resourceName: 'my-resource', fieldPattern: 'desc', seqno: 3, displayInd: 'N', userPattern: 'GRAILS_USER'],
        )
        testData.add(
            [resourceName: 'my-resource', fieldPattern: 'code', seqno: 2, displayInd: 'Y', userPattern: 'GRAILS_USER']
        )
        createTestData(testData)
        assertEquals 6, verifyCount()

        // test resource
        fieldPatterns = restContentFilterFields.retrieveFieldPatterns("my-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 2, fieldPatterns.size()
        assertEquals "desc", fieldPatterns.get(0)
        assertEquals "name", fieldPatterns.get(1)
    }


    @Test
    void testMissingSessionFactoryInjection() {
        restContentFilterFields.sessionFactory = null
        try {
            restContentFilterFields.retrieveFieldPatterns("my-resource")
            throw new RuntimeException("Expected an AssertionError")
        } catch(AssertionError e) {
            // ignore
        }
    }


    void teardownTestData() {
        def sql = new Sql(sessionFactory.currentSession.connection())
        sql.execute("delete from gordmsk where gordmsk_block_name = ?",['API'])
    }


    int verifyCount() {
        def sql = new Sql(sessionFactory.currentSession.connection())
        return sql.firstRow("select count(*) as cnt from gordmsk where gordmsk_block_name = ?",['API']).cnt.intValue()
    }


    void createTestData(List testData) {
        StringBuffer cmd = new StringBuffer(BEGIN_SQL_TEMPLATE)
        testData.each {
            cmd.append('  create_grodmsk(\'')
            cmd.append(it.resourceName)
            cmd.append('\', \'')
            cmd.append(it.fieldPattern)
            cmd.append('\', ')
            cmd.append(it.seqno)
            cmd.append(', \'')
            cmd.append(it.displayInd)
            cmd.append('\', \'')
            cmd.append(it.userPattern)
            cmd.append('\');\n')
        }
        cmd.append(END_SQL_TEMPLATE)
        def sql = new Sql(sessionFactory.currentSession.connection())
        sql.call(cmd.toString())
    }


    final static String BEGIN_SQL_TEMPLATE =
        """
DECLARE


  PROCEDURE create_gtvfbpr(p_code  gtvfbpr.gtvfbpr_code%TYPE,
                           p_desc  gtvfbpr.gtvfbpr_desc%TYPE) IS

  BEGIN

    INSERT into gtvfbpr
       (gtvfbpr_code,
        gtvfbpr_desc,
        gtvfbpr_activity_date,
        gtvfbpr_user_id)
      SELECT
        p_code,
        p_desc,
        SYSDATE,
        USER
       FROM dual
      WHERE NOT EXISTS
        (SELECT 1
           FROM gtvfbpr
          WHERE gtvfbpr_code = p_code);

  END;


  PROCEDURE create_gorfbpr(p_fgac_user_id  gorfbpr.gorfbpr_fgac_user_id%TYPE,
                           p_fbpr_code     gorfbpr.gorfbpr_fbpr_code%TYPE) IS

  BEGIN

    INSERT into gorfbpr
       (gorfbpr_fgac_user_id,
        gorfbpr_fbpr_code,
        gorfbpr_activity_date,
        gorfbpr_user_id)
      SELECT
        p_fgac_user_id,
        p_fbpr_code,
        SYSDATE,
        USER
       FROM dual
      WHERE NOT EXISTS
        (SELECT 1
           FROM gorfbpr
          WHERE gorfbpr_fgac_user_id = p_fgac_user_id
            AND gorfbpr_fbpr_code = p_fbpr_code);

  END;


  PROCEDURE create_grodmsk(p_resource_name  gordmsk.gordmsk_objs_comp_name%TYPE,
                           p_field_pattern  gordmsk.gordmsk_block_comp_name%TYPE,
                           p_sqno           gordmsk.gordmsk_seqno%TYPE,
                           p_display_ind    gordmsk.gordmsk_display_ind%TYPE,
                           p_user_pattern   VARCHAR2) IS

    lv_objs_code           gordmsk.gordmsk_objs_code%TYPE;
    lv_block_name          gordmsk.gordmsk_block_name%TYPE;
    lv_column_name         gordmsk.gordmsk_column_name%TYPE;
    lv_fgac_user_id        gordmsk.gordmsk_fgac_user_id%TYPE;
    lv_group_fgac_user_id  gordmsk.gordmsk_fgac_user_id%TYPE;
    lv_fbpr_code           gordmsk.gordmsk_fbpr_code%TYPE;
    lv_all_user_ind        gordmsk.gordmsk_all_user_ind%TYPE;

  BEGIN
    lv_objs_code := UPPER(REPLACE(SUBSTR('**API_'||p_resource_name,1,30),'-','_'));
    lv_block_name := 'API';
    lv_column_name := UPPER(REPLACE(REPLACE(REPLACE(p_field_pattern,'.','_'),'==','_'),'@',''));

    -- check if this field pattern applies to all users
    IF (UPPER(p_user_pattern) = '*') THEN
      lv_fgac_user_id := NULL;
      lv_group_fgac_user_id := NULL;
      lv_fbpr_code := NULL;
      lv_all_user_ind := 'Y';
    ELSE

      -- check if this field pattern applies to a user in a group (ex: user_id:group_id)
      IF (INSTR(p_user_pattern,':') > 0) THEN
        lv_fgac_user_id := NULL;
        lv_group_fgac_user_id := SUBSTR(p_user_pattern,1,INSTR(p_user_pattern,':',1,1)-1);
        lv_fbpr_code := SUBSTR(p_user_pattern,INSTR(p_user_pattern,':',-1,1)+1);
        lv_all_user_ind := 'N';
      ELSE

        -- this field pattern applies to an individual user
        lv_fgac_user_id := p_user_pattern;
        lv_group_fgac_user_id := NULL;
        lv_fbpr_code := NULL;
        lv_all_user_ind := 'N';

      END IF;

    END IF;

    IF (lv_fbpr_code IS NOT NULL) THEN
      create_gtvfbpr(lv_fbpr_code, lv_fbpr_code);
      create_gorfbpr(lv_group_fgac_user_id, lv_fbpr_code);
    END IF;

    INSERT into gordmsk
       (gordmsk_objs_code,
        gordmsk_block_name,
        gordmsk_column_name,
        gordmsk_seqno,
        gordmsk_display_ind,
        gordmsk_conceal_ind,
        gordmsk_objs_comp_name,
        gordmsk_block_comp_name,
        gordmsk_column_comp_name,
        gordmsk_fgac_user_id,
        gordmsk_fbpr_code,
        gordmsk_all_user_ind,
        gordmsk_activity_date,
        gordmsk_user_id)
      SELECT
        lv_objs_code,
        lv_block_name,
        lv_column_name,
        p_sqno,
        p_display_ind,
        'N',
        p_resource_name,
        p_field_pattern,
        '*',
        lv_fgac_user_id,
        lv_fbpr_code,
        lv_all_user_ind,
        SYSDATE,
        USER
       FROM dual
      WHERE NOT EXISTS
        (SELECT 1
           FROM gordmsk
          WHERE gordmsk_objs_code = lv_objs_code
            AND gordmsk_block_name = lv_block_name
            AND gordmsk_column_name = lv_column_name
            AND gordmsk_seqno = p_sqno);

  END;


BEGIN

  delete from gordmsk where gordmsk_block_name = 'API';
  delete from gorfbpr where gorfbpr_fbpr_code like 'API_TEST%_FPBR';
  delete from gtvfbpr where gtvfbpr_code like 'API_TEST%_FPBR';

"""


    final static String END_SQL_TEMPLATE =
            """
END;
"""

}
