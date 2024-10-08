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


import { 
    Button, 
    Flex, 
    Text, 
    View, 
    ActionButton,
    DialogTrigger, 
    Dialog,
    Heading, 
    Divider, 
    Content
} from '@adobe/react-spectrum';
import React,  {useState, useMemo} from 'react';
import { createEditor } from 'slate';
import { Slate, Editable, withReact } from 'slate-react';
import './styles.css';
import AutomatedSegment from '@spectrum-icons/workflow/AutomatedSegment';
import Gears from '@spectrum-icons/workflow/Gears';
import { withHistory } from 'slate-history';
import PromptConfig from './prompt-config';


const SmartPrompt = () => {

    const [editor] = useState(() => withReact(createEditor()))
    const initialValue = [
        {
          type: 'paragraph',
          children: [{ text: 'Describe the kind of social post you want to create.' }],
        },
      ];

    const handlePromptChange = (text) => {
       editor.removeNodes();
    }

    return(
        <div style={{ 
            width: '100%',
            backgroundColor: 'black',
            borderRadius:'10px',
            padding: '10px'
        }} className='smart-prompt-container'>
            <Flex direction={'row'} alignItems={'center'} gap={20}>
                <View width={'90%'}>
                <Flex direction={'column'}>
                    <div className='prompt-label'>
                        <Text>Prompt</Text>
                        <PromptConfig onPromptChange={(prompt) => handlePromptChange(prompt)}/>
                    </div>
                    <Slate editor={editor} initialValue={initialValue} className='prompt-input'>
                        <Editable style={{outline: 0}}/>
                    </Slate>
                </Flex>
                </View>
               
                <Button variant="accent" style="fill" isDisabled height={20}>
                    <AutomatedSegment />
                    <Text>Generate</Text>
                </Button>
            </Flex>
        </div>
    );

}

export default SmartPrompt;

