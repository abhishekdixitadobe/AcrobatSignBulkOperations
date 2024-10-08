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

import { Flex, Heading, View } from '@adobe/react-spectrum';
import React from 'react';

const Firefall = () => {
    return(
        <Flex direction={'column'} height={'100%'}>
            <Heading level={2}>Marketing Copy</Heading>
            <View borderWidth={'thin'} height={'100%'} marginTop={30}>
                <Flex direction={'row'} alignItems={'center'} height={'100%'}>
                    <Flex direction={'column'} width={'100%'} alignItems={'center'}>
                    <Heading level={3}>Placeholder for post copy from Firefall API</Heading>
                    </Flex>
                    
                </Flex>
            </View>
        </Flex>
    );
}

export default Firefall;