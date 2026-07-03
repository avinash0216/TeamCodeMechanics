import { useContext, useState } from "react";
import GeneralForm from "../components/common/generalForm";
import { TitleContext } from "../components/common/TitleContext";
import { Account } from "../api/types";

export default function Payment({ title, accounts }: { title: string | null; accounts: Account[] }) {
  const paymentCompanies: IPaymentFacilities[] = 
  [
    { id: 1, name: 'Electricity' },
    { id: 2, name: 'Water' },
    { id: 3, name: 'Internet' },
    { id: 4, name: 'Gas' },
    { id: 5, name: 'Phone' }
  ];
  const [selectedCompany, setSelectedCompany] = useState<IPaymentFacilities | null>(null);
  const btnTitle = 'Submit Payment';
  const labelDescription = 'From Account';
  
  return (
    <div className="payment-form">
      <GeneralForm  paymentComps={paymentCompanies} selectedValue={selectedCompany?.id}
      onChange={setSelectedCompany} btnTitle={btnTitle} labelDescription={labelDescription} />
    </div>
  );
}

export interface IPaymentFacilities {
  id: number;
  name: string;
}