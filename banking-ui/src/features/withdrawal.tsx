import GeneralForm from "../components/common/generalForm";


export default function Withdrawal() {

  const btnTitle = 'Withdraw';
  const labelDescription = 'From Account';

  return (
          <div className="withdrawal-form">
            <GeneralForm btnTitle={btnTitle} labelDescription={labelDescription} />
          </div>
        );
  
}