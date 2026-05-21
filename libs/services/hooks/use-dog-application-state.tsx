import type { Profile } from "@repo/profile";
import {
  type Dispatch,
  type ReactNode,
  createContext,
  useContext,
  useReducer,
} from "react";
import type {
  Dog,
  DogApplication,
  DogApplicationState,
} from "../models/dog-application";
import type { Service } from "../models/services";

const initialState = {
  profile: undefined,
  service: undefined,
  dog: undefined,
  justification: undefined,
};

const DogApplicationStateContext = createContext<{
  state: DogApplicationState;
  dispatch: Dispatch<DogApplicationAction>;
}>({
  state: initialState,
  dispatch: () => {},
});

interface DogApplicationAction {
  type:
    | "ADD_PROFILE"
    | "ADD_SERVICE"
    | "ADD_DOG"
    | "ADD_JUSTIFICATION"
    | "RESET";
  payload?: unknown;
}

const dogApplicationReducer = (
  state: DogApplicationState,
  action: DogApplicationAction,
): DogApplicationState => {
  switch (action.type) {
    case "ADD_PROFILE":
      return {
        ...state,
        profile: action.payload as Profile,
      };
    case "ADD_SERVICE":
      return {
        ...state,
        service: action.payload as Service,
      };
    case "ADD_DOG":
      return {
        ...state,
        dog: action.payload as Dog,
      };
    case "ADD_JUSTIFICATION":
      return {
        ...state,
        justification: action.payload as DogApplication["justification"],
      };
    case "RESET":
      return initialState;
    default:
      return state;
  }
};

export const DogApplicationProvider = ({
  children,
}: { children: ReactNode }) => {
  const [state, dispatch] = useReducer(dogApplicationReducer, initialState);

  return (
    <DogApplicationStateContext.Provider value={{ state, dispatch }}>
      {children}
    </DogApplicationStateContext.Provider>
  );
};

export const useDogApplicationState = () =>
  useContext(DogApplicationStateContext);
