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

import React from 'react';
import { Flex, View, Heading } from '@adobe/react-spectrum';

import background from './images/sunset_background.jpg';
import SmartPrompt from '../../components/prompt';


const agreements = ({onContextChange}) => {
    const handlePayloadUpdate = (payload) => {
        onContextChange(payload);
    }
    return(
        <div className='hero-banner' style={{
            backgroundImage: `linear-gradient(rgba(255,255,255,.3), rgba(255,255,255,.3)), url(${background})`,
            backgroundRepeat: 'no-repeat',
            height: '100%',
            width: '100%',
            backgroundSize: '100% 100%',
            borderRadius:'10px'
        }}>
            <Flex direction={'column'} height={'100%'} width={'100%'} alignItems={'center'}>
                <Flex direction={'row'} height={'100%'} alignItems={'center'}>
                    <Heading level={1}>Agreements</Heading>
                </Flex>
          
            </Flex>
        </div>

    );
}

export default agreements;
