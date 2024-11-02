import React, { useState, useRef } from "react";
import { Flex, DatePicker, TextField, Button, Item, ComboBox, DropZone, IllustratedMessage, Heading, Content, FileTrigger } from "@adobe/react-spectrum";
import Upload from '@spectrum-icons/illustrations/Upload';
import AgreementAction from "../../components/agreement-action";
import { parseDate } from "@internationalized/date";
import { useSelector, useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";

const WorkflowForm = () => {
    const [startDate, setStartDate] = useState(parseDate("2020-02-03"));
    const [endDate, setEndDate] = useState(parseDate("2020-02-03"));
    const [email, setEmail] = useState("");

    const authState = useSelector((state) => state.auth || {});
    const isAuthenticated = authState.isAuthenticated || false;
    const user = authState.user;
    const navigate = useNavigate();
    const dispatch = useDispatch();

    const configs = {
        page: "/agreements",
        api: "/api/agreements",
    };

    const handleApiCall = async (params) => {
        const { startDate, endDate, email } = params;
        try {
            const apiUrl = `/api/search`;
            const reqBody = {
                startDate: startDate,
                endDate: endDate,
                email: email,
                selectedStatus: Array.from(selectedStatus),
            };
            const response = await fetch(apiUrl, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: "Bearer ",
                },
                body: JSON.stringify(reqBody),
            });

            if (response.ok) {
                const data = await response.json();
                console.log("Agreements Data: ", data);
            } else {
                console.error("API call failed", response.statusText);
            }
        } catch (error) {
            console.error("Error making API call:", error);
        }
    };

    let options = [
        { id: 1, name: 'Aerospace' },
        { id: 2, name: 'Mechanical' },
        { id: 3, name: 'Civil' },
        { id: 4, name: 'Biomedical' },
        { id: 5, name: 'Nuclear' },
        { id: 6, name: 'Industrial' },
        { id: 7, name: 'Chemical' },
        { id: 8, name: 'Agricultural' },
        { id: 9, name: 'Electrical' }
    ];
    let [majorId, setMajorId] = React.useState(null);
    let [isFilled1, setIsFilled1] = React.useState(false);
    let [isFilled2, setIsFilled2] = React.useState(false);


    return (
        <>
            <Flex direction="column" gap="size-100">
                <Flex direction="row" gap="size-100">
                    <DatePicker label="Start Date" value={startDate} onChange={setStartDate} />
                    <DatePicker label="End Date" value={endDate} onChange={setEndDate} />
                    <ComboBox
                        label="WorkFlows"
                        defaultItems={options}
                        onSelectionChange={setMajorId}>
                        {item => <Item>{item.name}</Item>}
                    </ComboBox>
                    <AgreementAction
                        params={{ startDate, endDate, email }}
                        onAction={handleApiCall}
                        buttonText="Get Agreements"
                        isDisabled={!email}
                        configs={configs}
                        heading="Agreements"
                    />
                </Flex>
                <DropZone
                    maxWidth="size-6000"
                    isFilled={isFilled1}
                    onDrop={() => setIsFilled1(true)}>
                    <IllustratedMessage>
                        <Upload />
                        <Heading>
                            {isFilled1 ? 'You dropped something!' : 'Drag and drop file here'}
                        </Heading>
                        <Content>
                            <FileTrigger
                                onSelect={() => setIsFilled1(true)}>
                                <Button variant="primary">Browse</Button>
                            </FileTrigger>
                        </Content>
                    </IllustratedMessage>
                </DropZone>
            </Flex>
        </>
    );
};

export default WorkflowForm;