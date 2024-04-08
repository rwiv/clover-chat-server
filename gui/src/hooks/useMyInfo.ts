import {useNavigate} from "react-router";
import {useEffect} from "react";
import {useQuery} from "@apollo/client";
import {myInfoQL} from "@/client/account.ts";
import {Query} from "@/graphql/types.ts";

export function useMyInfo() {

  const {data: myInfoData, error} = useQuery<Query>(myInfoQL);
  const myInfo = myInfoData?.account;

  const navigate = useNavigate();

  useEffect(() => {
    if (error) navigate("/account-select");
  }, [error]);

  return {myInfo, error};
}