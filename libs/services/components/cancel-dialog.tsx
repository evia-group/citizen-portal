import {
  Button,
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  Text,
  useDisclosure,
} from "@repo/ui";
import { router } from "expo-router";
import { View } from "react-native";
import { useDogApplicationState } from "../hooks/use-dog-application-state";

export function CancelDialog() {
  const [isOpen, { open, close }] = useDisclosure(false);
  const { dispatch } = useDogApplicationState();

  return (
    <Dialog
      open={isOpen}
      onOpenChange={(value: boolean) => {
        value ? open() : close();
      }}
    >
      <Button variant="secondary" onPress={open}>
        <Text>Abbrechen</Text>
      </Button>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>
            <Text className="font-bold">Abbrechen</Text>
          </DialogTitle>
        </DialogHeader>
        <View>
          <View className="flex flex-row gap-4 mb-6">
            <Text>
              Sind sich sicher, dass Sie diesen Online-Antrag abbrechen möchten?
            </Text>
          </View>

          <View className="flex flex-row justify-between gap-4">
            <Button variant="secondary" onPress={close}>
              <Text>Antrag fortführen</Text>
            </Button>
            <Button
              variant="default"
              onPress={() => {
                dispatch({ type: "RESET" });
                close();
                router.navigate("./");
              }}
            >
              <Text>Ja, abbrechen</Text>
            </Button>
          </View>
        </View>
      </DialogContent>
    </Dialog>
  );
}
