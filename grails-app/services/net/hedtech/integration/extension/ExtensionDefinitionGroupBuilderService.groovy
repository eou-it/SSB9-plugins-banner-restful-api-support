/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

/**
 * Builder class to create a list of grouped Extension Definitions
 */
class ExtensionDefinitionGroupBuilderService {

    /**
     * Build a list of groups
     * @param extensionDefinitionList
     * @return
     */
    List<ExtensionDefinitionGroup> build(def extensionDefinitionList){
        List<ExtensionDefinitionGroup> extensionDefinitionGroupList = null;

        if (extensionDefinitionList)
        {
            extensionDefinitionGroupList = []
            for (ExtensionDefinition extensionDefinition : extensionDefinitionList) {
                ExtensionDefinitionGroup extensionDefinitionGroup = this.getGroup(extensionDefinitionGroupList,extensionDefinition)
                if (extensionDefinitionGroup){
                    extensionDefinitionGroup.extensionDefinitionList.add(extensionDefinition)
                }else{
                    ExtensionDefinitionGroup newExtensionDefinitionGroup = buildGroup(extensionDefinition)
                    extensionDefinitionGroupList.add(newExtensionDefinitionGroup)
                }
            }

        }
        return extensionDefinitionGroupList
    }

    /**
     * Function to look for a group on in the list
     * @param extensionDefinitionGroupList
     * @param sqlProcessCode
     * @param sqlRuleCode
     * @return
     */
    private ExtensionDefinitionGroup getGroup(def extensionDefinitionGroupList, ExtensionDefinition extensionDefinition){
        ExtensionDefinitionGroup extensionDefinitionGroup = null
        if (extensionDefinitionGroupList && extensionDefinition){
            for (ExtensionDefinitionGroup item : extensionDefinitionGroupList) {
                if (item.sqlProcesCode == extensionDefinition.sqlProcessCode &&
                        item.sqlRuleCode == extensionDefinition.sqlRuleCode) {
                    extensionDefinitionGroup = item
                    break
                }
            }
        }
        return extensionDefinitionGroup
    }

    /**
     * Function to build a new group based on the extension definition
     * @param extensionDefinition
     * @return
     */
    private ExtensionDefinitionGroup buildGroup(ExtensionDefinition extensionDefinition){
        ExtensionDefinitionGroup extensionDefinitionGroup = null
        if (extensionDefinition){
            extensionDefinitionGroup = new ExtensionDefinitionGroup()
            extensionDefinitionGroup.sqlProcesCode = extensionDefinition.sqlProcessCode
            extensionDefinitionGroup.sqlRuleCode = extensionDefinition.sqlRuleCode
            extensionDefinitionGroup.extensionDefinitionList = []
            extensionDefinitionGroup.extensionDefinitionList.add(extensionDefinition)
        }

        return extensionDefinitionGroup
    }
}
