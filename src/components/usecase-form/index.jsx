import React from "react";
import BulkOpForm from "./bulkOpForm";
import DropZoneForm from "./dropZoneForm";
import BannerDropZoneForm from "./bannerDropZoneForm";
import AgreementForm from "./agreementForm"
import WorkflowForm from "./workflowForm";
import TemplateForm from "./templateForm";
import WebForm from "./webForm";

const UseCaseForm = ({ id, onFormChange, setUploadFiles }) => {
  switch (id) {
    case "BulkOpForm":
      return (
        <BulkOpForm onChange={onFormChange} setUploadFiles={setUploadFiles} />
      );
    case "agreements":
      return (
        <AgreementForm
          action="agreements"
          onChange={onFormChange}
          setUploadFiles={setUploadFiles}
        />
      );
    case "workflows":
      return (
        <WorkflowForm
          action="workflows"
          onChange={onFormChange}
          setUploadFiles={setUploadFiles}
        />
      );
    case "templates":
      return (
        <TemplateForm
          action="templates"
          onChange={onFormChange}
          setUploadFiles={setUploadFiles}
        />
      );
    case "webforms":
      return (
        <WebForm
          action="webforms"
          onChange={onFormChange}
          setUploadFiles={setUploadFiles}
        />
      );
    case "BrandedContentGeneration":
      return (
        <BannerDropZoneForm
          action="BrandedContentGeneration"
          onChange={onFormChange}
          setUploadFiles={setUploadFiles}
        />
      );
    case "ProductPlacement":
      return (
        <DropZoneForm
          action="ProductPlacement"
          onChange={onFormChange}
          showCSVDropZone
        />
      );
    default:
      return <>Nothing to be display</>;
  }
};

export default UseCaseForm;
