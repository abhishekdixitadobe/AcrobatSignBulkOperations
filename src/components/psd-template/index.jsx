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

import React, {useEffect} from 'react';
import {Flex, View, Image, ActionButton, Heading} from '@adobe/react-spectrum';
import TemplateImage from './template.png';
import TemplatePsd from './template.psd';
import {toBase64} from '../../utils/fileUtils';


const PhotoshopTemplate = () => {

    const [ccEverywhere, setCCEverywhere] = React.useState(null);
    const [result, setResult] = React.useState(null);
    const [projectId, setProjectId] = React.useState(null);

    // Initialize EmbedSDK on view load
  /*  useEffect(() => {
        const params = {
            clientId: REACT_APP_EXPRESS_CLIENT_ID,
            appName: REACT_APP_EXPRESS_APP_NAME
        };

        const script = document.createElement('script');
        script.src = REACT_APP_EXPRESS_SDK_CDN;
        script.onload = async() => {
            if(!window.CCEverywhere) {
                return;
            }
            const sdk = await window.CCEverywhere.initialize(params);
            setCCEverywhere(sdk);
        }
        document.body.appendChild(script);
    },[]);*/
    const createDesignCallbacks = {
        onCancel: () => {},
        onPublish: (publishParams) => {
            const localData = { project: publishParams.asset[0].projectId, image: publishParams.asset[0].data };
            setResult(localData.image);
            setProjectId(localData.project);
        },
        onError: (err) => {
            console.error('Error received is', err.toString());
        }
    }

    const toBase64 = (file) => new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result);
        reader.onerror = reject;
    });

    const handleClick = async () => {
        //const url = new URL(`file://TemplatePsd);
        const res = await fetch(TemplatePsd);
        const buffer = await res.blob();
        const contents = await toBase64(buffer);


        ccEverywhere.createDesign(
            {
                callbacks: createDesignCallbacks,
                outputParams: {
                    outputType: "base64"
                },
                inputParams: {
                    asset: contents
                }
            }
        );

    }

    return(
        <Flex direction={'column'} gap={30}>
            <Flex direction={'row'} alignItems={'center'}>
                <View width={'100%'}>
                    <Heading level={2}>Photoshop Template</Heading>
                </View>
                <ActionButton width={200} onPress={handleClick}>Open in Express</ActionButton>
            </Flex>
            <Image src={TemplateImage} height={350}/>
        </Flex>

    );
}

export default PhotoshopTemplate;
