import UploadDocument from "../components/UploadDocument";
import UploadSignature from "../components/UploadSignature";
import PdfViewer from "../components/PdfViewer";

function Dashboard() {
  return (
    <div>
      <h1>Digital Signature App</h1>

      <UploadDocument />

      <hr />

      <UploadSignature />

      <hr />

      <PdfViewer />
    </div>
  );
}

export default Dashboard;