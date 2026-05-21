import { type Profile, useProfile } from "@repo/profile";
import { LoadingIndicator, Text } from "@repo/ui";
import { Stack } from "expo-router";
import { View } from "react-native";
import { useDogApplicationState } from "../hooks/use-dog-application-state";
import type {
  Dog,
  DogApplication,
  DogApplicationState,
} from "../models/dog-application";
import type { Service } from "../models/services";
import { Confirmation } from "./pages/confirmation";
import { DmsData } from "./pages/dms-data";
import { DogData } from "./pages/dog-data";
import { Overview } from "./pages/overview";
import { type NonOptional, PaymentData } from "./pages/payment-data";
import { PersonalData } from "./pages/personal-data";

interface Props {
  service: Service;
  page?: number;
}

export function Details({ service, page }: Props) {
  const {
    data: profile,
    isLoading: profileIsLoading,
    isError: profileIsError,
    error: profileError,
  } = useProfile(1);

  const { state, dispatch } = useDogApplicationState();

  const addProfile = (profile: Profile) => {
    dispatch({ type: "ADD_PROFILE", payload: profile });
  };
  const addService = (service: Service) => {
    dispatch({ type: "ADD_SERVICE", payload: service });
  };
  const addDog = (dog: Dog) => {
    dispatch({ type: "ADD_DOG", payload: dog });
  };
  const addJustification = (justification: DogApplication["justification"]) => {
    dispatch({ type: "ADD_JUSTIFICATION", payload: justification });
  };

  return (
    <View className="p-4">
      <Stack.Screen
        options={{
          title: service.name,
        }}
      />
      {!page && <Overview service={service} />}
      {profileIsLoading && <LoadingIndicator />}
      {profileIsError && (
        <Text>Ein Fehler ist aufgetreten {profileError.message}</Text>
      )}
      {page === 1 && profile != null && (
        <PersonalData
          profile={profile}
          service={service}
          addService={addService}
          addProfile={addProfile}
        />
      )}
      {page === 2 && profile != null && (
        <DogData
          addDog={addDog}
          addJustification={addJustification}
          profile={profile}
          addProfile={addProfile}
        />
      )}
      {page === 3 && <DmsData />}
      {page === 4 && profile != null && (
        <PaymentData
          service={service}
          dogApplicationState={state as NonOptional<DogApplicationState>}
        />
      )}
      {page === 5 && <Confirmation />}
    </View>
  );
}
