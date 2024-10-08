/* ************************************************************************
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

import React, { useState } from 'react';
import { Flex, View, Dialog, DialogContainer, Content, ProgressCircle, Heading, Text } from '@adobe/react-spectrum';
import PropertyRail from './prop-rail';
import { useLocation } from 'react-router-dom';
import JobConfig from './job-config';
import { useDispatch, useSelector } from 'react-redux';
import { setEndTimestamp, setJobStatus, setStartTimestamp } from '../../redux/apis';
import { CCAPIActions } from '../../services/actions';
import PreviewResults from '../../components/preview';
import ImageResults from '../../components/image-results';
import { getSignedURL } from '../../utils/aws-client';


const SetupView = () => {
    const segments = useSelector((state) => state.app.segments);
    const regions = useSelector((state) => state.app.segments);
    const selectedBackground = useSelector((state) => state.app.selectedBackground);
    const jobStatus = useSelector((state) => state.apis.jobStatus);
    const currentCall = useSelector((state) => state.apis.currentCall);
    
    const dispatch = useDispatch();
    const location = useLocation();

    const [inputFile, setInputFile] = useState(null);
    const [inProgress, setInProgress] = useState(false);
    const [previewImages, setPreviewImages] = useState(null);
    const [finalOutput, setFinalOutput] = useState(null);

    const visualizeProduct = async () => {
        dispatch(setJobStatus('running'));
        dispatch(setStartTimestamp(Date.now()));
        const ccAPI = new CCAPIActions(dispatch);
        await ccAPI.init();
        const productMaskURL = await ccAPI.createMaskFromImage(inputFile);
        
        // Pull the mask from AWS storage, and upload to Firefly
        const mask = await fetch(productMaskURL);
        const blob = await mask.blob();
        const maskFile = new File([blob], 'mask.png', {type: 'image/png'});

        const images = await ccAPI.generativeFill('Soft sunset at the end of a long winding road', inputFile, maskFile);
        setPreviewImages(images);
        dispatch(setJobStatus('preview'));
        dispatch(setEndTimestamp(Date.now()));
    }

    const renderFinal = async (image) => {
            dispatch(setJobStatus('running'));
            setInProgress(true);
            const ccAPI = new CCAPIActions(dispatch);
            await ccAPI.init();
            const inputPSDTemplateUrl = await getSignedURL('getObject', 'wip/template.psd');
            const finalImageURL = await ccAPI.composeImagefromPSD(inputPSDTemplateUrl,image,'Hello Kumar');
            setFinalOutput([finalImageURL]);
            setInProgress(false);
            dispatch(setJobStatus('final'));
    }

    const runWorkflow = async () => {
        setInProgress(true);
        dispatch(setJobStatus('running'))
        await visualizeProduct();
        setInProgress(false);
    }

    const onImageDrop = (inputFile) => {
        setInputFile(inputFile);
    } 

    return (
        <Flex direction={'row'} gap={50} height={'100%'} width={'100%'}>
                {jobStatus === 'ready'  &&
                    <JobConfig onImageDrop={onImageDrop}/>
                }
                {jobStatus === 'running' &&
                    <Flex direction={'column'} width={'100%'} height={'100%'} alignItems={'center'}>
                        <Flex direction={'row'} width={'100%'} alignItems={'center'}>
                            <DialogContainer onDismiss={()=> setInProgress(false)}>
                                {inProgress && <Dialog size='M'>
                                    <Content>
                                        <Flex direction='column' alignItems='center' height='100%' width='100%' gap='size-100'>
                                            <ProgressCircle size='L' aria-label="Hold on, magic is happening…" isIndeterminate/>
                                            <Text>Hold on, magic is happening…</Text>
                                            <Heading level={1}>{currentCall ? currentCall.name : ''}</Heading>
                                        </Flex>
                                    </Content>
                                </Dialog>}
                            </DialogContainer>
                        </Flex>
                    </Flex>
                }
                {jobStatus === 'preview' &&
                    <PreviewResults images={previewImages} onContinue={renderFinal}/>
                }
                {jobStatus === 'final' &&
                     <ImageResults images={finalOutput}/>
                }
                
            
            <View height={'100%'}>
                <PropertyRail context={location.state} width={'size-6000'} onExecute={runWorkflow} />
            </View>
        </Flex>
    );
}

export default SetupView