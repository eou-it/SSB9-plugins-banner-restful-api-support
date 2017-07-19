package net.hedtech.integration.extension

/**
 * Created by sdorfmei on 7/17/17.
 */
class ExtractedExtensionPropertyGroupBuilderService {

    /**
     * Build a list of groups
     * @param extractedExtensionPropertyList
     * @return
     */
    List<ExtractedExtensionPropertyGroup> build(def extractedExtensionPropertyList){
        List<ExtractedExtensionPropertyGroup> extractedExtensionPropertyGroupList = null;

        if (extractedExtensionPropertyList)
        {
            extractedExtensionPropertyGroupList = []
            for (ExtractedExtensionProperty extractedExtensionProperty : extractedExtensionPropertyList) {
                ExtractedExtensionPropertyGroup extractedExtensionPropertyGroup = this.getGroup(extractedExtensionPropertyGroupList,extractedExtensionProperty)
                if (extractedExtensionPropertyGroup){
                    extractedExtensionPropertyGroup.extractedExtensionPropertyList.add(extractedExtensionProperty)
                }else{
                    ExtractedExtensionPropertyGroup newExtractedExtensionPropertyGroup = buildGroup(extractedExtensionProperty)
                    extractedExtensionPropertyGroupList.add(newExtractedExtensionPropertyGroup)
                }
            }
        }
        return extractedExtensionPropertyGroupList
    }

    /**
     * Function to look for a group on in the list
     * @param extractedExtensionPropertyGroupList
     * @param extractedExtensionProperty
     * @return
     */
    private ExtractedExtensionPropertyGroup getGroup(def extractedExtensionPropertyGroupList, ExtractedExtensionProperty extractedExtensionProperty){
        ExtractedExtensionPropertyGroup extractedExtensionPropertyGroup = null
        if (extractedExtensionPropertyGroupList && extractedExtensionProperty){
            for (ExtractedExtensionPropertyGroup item : extractedExtensionPropertyGroupList) {
                ExtensionDefinition extensionDefinition = extractedExtensionProperty.extendedDefinition
                if (extensionDefinition){
                    if (item.sqlProcessCode == extensionDefinition.sqlProcessCode &&
                            item.sqlRuleCode == extensionDefinition.sqlWriteRuleCode) {
                        extractedExtensionPropertyGroup = item
                        break
                    }
                }
            }
        }
        return extractedExtensionPropertyGroup
    }

    /**
     * Function to build a new group based on the extension definition
     * @param extensionDefinition
     * @return
     */
    private ExtractedExtensionPropertyGroup buildGroup(ExtractedExtensionProperty extractedExtensionProperty){
        ExtractedExtensionPropertyGroup extractedExtensionPropertyGroup = null
        if (extractedExtensionProperty){
            if (extractedExtensionProperty.extendedDefinition){
                ExtensionDefinition extensionDefinition = extractedExtensionProperty.extendedDefinition
                extractedExtensionPropertyGroup = new ExtractedExtensionPropertyGroup()
                extractedExtensionPropertyGroup.sqlProcessCode = extensionDefinition.sqlProcessCode
                extractedExtensionPropertyGroup.sqlRuleCode = extensionDefinition.sqlWriteRuleCode
                extractedExtensionPropertyGroup.extractedExtensionPropertyList = []
                extractedExtensionPropertyGroup.extractedExtensionPropertyList.add(extractedExtensionProperty)
            }

        }

        return extractedExtensionPropertyGroup
    }
}
