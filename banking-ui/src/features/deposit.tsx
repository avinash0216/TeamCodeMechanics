import GeneralForm from "../components/common/generalForm";

const btnTitle = 'Deposit';
const labelDescription = 'To Account';

export default function Deposit() {
  return (
      <div className="deposit-form">
        <GeneralForm btnTitle={btnTitle} labelDescription={labelDescription} />
      </div>
    );
}