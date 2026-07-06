import GeneralForm from "../components/common/generalForm";

const btnTitle = 'Deposit';
const labelDescription = 'To Account';

export default function Deposit({ onActionComplete }: { onActionComplete?: () => void }) {
  return (
      <div className="deposit-form">
        <GeneralForm btnTitle={btnTitle} labelDescription={labelDescription} onActionComplete={onActionComplete ?? (() => {})} />
      </div>
    );
}