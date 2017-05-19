package net.hedtech.integration.extension

import org.junit.After
import org.junit.Before
import org.junit.Test
import net.hedtech.banner.testing.BaseIntegrationTestCase
/**
 * Created by sdorfmei on 5/19/17.
 */
class ResourceIdListBuilderServiceTests  extends BaseIntegrationTestCase {

    def resourceIdListBuilderService

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
     void testNullContent() {
         def resourceIdList = resourceIdListBuilderService.buildFromContentList(null)
         assertNull resourceIdList

     }

    /*@Test
    void testOneResourceContent() {
        def oneResource = '''{"id": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce"}'''


        def resultList = resourceIdListBuilderService.buildFromContentList(oneResource)
        assertNotNull resultList
        assertTrue resultList.size == 3
    }*/

    @Test
    void testManyResourceContent() {
        def arrayOfResources = '''
                [{"id": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce"},
                 {"id": "26a2673f-9bc6-4649-a3e8-213d0ff4afbd"},
                 {"id": "26a2243f-9af6-4649-a3e8-213222f4afsf"}
          
                ]'''


        def resultList = resourceIdListBuilderService.buildFromContentList(arrayOfResources)
        assertNotNull resultList
        assertTrue resultList.size == 3
    }
}
