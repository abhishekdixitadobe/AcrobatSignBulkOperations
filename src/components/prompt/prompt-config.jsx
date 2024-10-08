/*************************************************************************
 * ADOBE CONFIDENTIAL
 * ___________________
 *
 * Copyright 2024 Adobe
 * All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains
 * the property of Adobe and its suppliers, if any. The intellectual
 * and technical concepts contained herein are proprietary to Adobe
 * and its suppliers and are protected by all applicable intellectual
 * property laws, including trade secret and copyright laws.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe.
**************************************************************************/

import React, {useState} from 'react';
import { 
    Button, 
    Flex, 
    Text, 
    View, 
    Picker,
    ListBox,
    ActionButton,
    DialogTrigger, 
    Dialog,
    Heading, 
    Divider, 
    Content,
    Item,
} from '@adobe/react-spectrum';
import Gears from '@spectrum-icons/workflow/Gears';
import { audienceArray, regionArray, productArray, getName, buildList } from '../../utils/data';



const PromptConfig = ({onContextChange}) => {
    const [audiences, setAudiences] = useState([]);
    const [product, setProduct] = useState(null);
    const [regions, setRegions] = useState([]);

    

    const handleApply = (close) => {
        close();
        const selectedAudiences = Array.from(audiences);
        const selectedRegions = Array.from(regions);

        const promptString = `Create a post for ${getName(productArray, product)} targeted at ${buildList(audienceArray, selectedAudiences)} in ${buildList(regionArray, selectedRegions)}`;
        const payload = {
            prompt: promptString,
            product: product,
            audiences: audiences,
            regions: regions
        }
        onContextChange(payload);
    }

    return(
        <DialogTrigger type='tray'>
            <ActionButton isQuiet height={18}><Gears/></ActionButton>
            {(close) => (
            <Dialog>
                <Heading level={2}>Prompt Template</Heading>
                <Divider />
                <Content>
                    <Flex direction={'column'} gap={20}>
                        <Picker items={productArray} label='Product' labelPosition='side' onSelectionChange={setProduct}>
                            {(item) => <Item key={item.id}>{item.name}</Item>}
                        </Picker>
                        <View>
                            <Text>Audiences</Text>
                            <ListBox items={audienceArray}  selectionMode="multiple" onSelectionChange={setAudiences} aria-label='audience'>
                                {(item) => <Item key={item.id}>{item.name}</Item>}
                            </ListBox>
                        </View>
                        <View>
                            <Text>Regions</Text>
                            <ListBox items={regionArray}  selectionMode="multiple" onSelectionChange={setRegions} aria-label='regions'>
                                {(item) => <Item key={item.id}>{item.name}</Item>}
                            </ListBox>
                        </View>
                        <Button variant="accent" style="fill" onPress={() => handleApply(close)}>Apply</Button>
                    </Flex>
                </Content>
            </Dialog>
            )}
        </DialogTrigger>
        
    )
}

export default PromptConfig;