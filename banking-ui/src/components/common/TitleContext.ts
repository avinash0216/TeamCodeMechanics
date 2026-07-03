import React from "react";
import { Account } from "../../api/types";

export const TitleContext = React.createContext<string | null>(null);
export const AccountsContext = React.createContext<Account[] | null>(null);