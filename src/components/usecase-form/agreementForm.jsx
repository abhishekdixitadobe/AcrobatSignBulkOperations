import React, { useState, useRef } from "react";
import { Flex, DatePicker, TextField, Button, Item, ComboBox, DropZone, IllustratedMessage, Heading, Content, FileTrigger } from "@adobe/react-spectrum";
import Upload from '@spectrum-icons/illustrations/Upload';
import AgreementAction from "../../components/agreement-action";
import { parseDate } from "@internationalized/date";
import { useSelector, useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";

const AgreementForm = () => {
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
      <div class="accordion accordion-flush" id="accordionFlushExample">
        <div class="accordion-item">
          <h2 class="accordion-header" id="flush-headingOne">
            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#flush-collapseOne" aria-expanded="false" aria-controls="flush-collapseOne">
              Agreements will be fetched between this date range.
            </button>
          </h2>
          <div id="flush-collapseOne" class="accordion-collapse collapse" aria-labelledby="flush-headingOne" data-bs-parent="#accordionFlushExample">
            <div class="accordion-body">
              <Flex gap="size-200" wrap>
                <DatePicker label="Start Date" value={startDate} onChange={setStartDate} />
                <DatePicker label="End Date" value={endDate} onChange={setEndDate} />
                <TextField
                  label="User Email"
                  value={email}
                  onChange={setEmail}
                  type="email"
                />
                <AgreementAction
                  params={{ startDate, endDate, email }}
                  onAction={handleApiCall}
                  buttonText="Get Agreements"
                  isDisabled={!email}
                  configs={configs}
                  heading="Agreements"
                />
              </Flex>
            </div>
          </div>
        </div>
        <div class="accordion-item">
          <h2 class="accordion-header" id="flush-headingTwo">
            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#flush-collapseTwo" aria-expanded="false" aria-controls="flush-collapseTwo">
              Upload Users List
            </button>
          </h2>
          <div id="flush-collapseTwo" class="accordion-collapse collapse" aria-labelledby="flush-headingTwo" data-bs-parent="#accordionFlushExample">
            <div class="accordion-body">
              <Flex direction="column" gap="size-200">
                <Flex direction="row" gap="size-200">
                  <DatePicker label="Start Date" value={startDate} onChange={setStartDate} />
                  <DatePicker label="End Date" value={endDate} onChange={setEndDate} />
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
            </div>
          </div>
        </div>
        <div class="accordion-item">
          <h2 class="accordion-header" id="flush-headingThree">
            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#flush-collapseThree" aria-expanded="false" aria-controls="flush-collapseThree">
              Upload Aggrement IDs List
            </button>
          </h2>
          <div id="flush-collapseThree" class="accordion-collapse collapse" aria-labelledby="flush-headingThree" data-bs-parent="#accordionFlushExample">
            <div class="accordion-body">
              <Flex direction="column" gap="size-200">
                <DropZone
                  isFilled={isFilled2}
                  onDrop={() => setIsFilled2(true)}>
                  <IllustratedMessage>
                    <Upload />
                    <Heading>
                      {isFilled2 ? 'You dropped something!' : 'Drag and drop file here'}
                    </Heading>
                    <Content>
                      <FileTrigger
                        onSelect={() => setIsFilled2(true)}>
                        <Button variant="primary">Browse</Button>
                      </FileTrigger>
                    </Content>
                  </IllustratedMessage>
                </DropZone>
                <AgreementAction
                  params={{ startDate, endDate, email }}
                  onAction={handleApiCall}
                  buttonText="Get Agreements"
                  isDisabled={!email}
                  configs={configs}
                  heading="Agreements"
                />
              </Flex>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default AgreementForm;