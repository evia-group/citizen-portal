import { getDocumentDownloadLink, useDocuments } from "@repo/documents";
import { Button, Input, LoadingIndicator } from "@repo/ui";
import { openBrowserAsync } from "expo-web-browser";
import { Download, Search } from "lucide-react-native";
import { useForm } from "react-hook-form";
import { Platform, Pressable, Text, View } from "react-native";

export default function DmsScreen() {
  const { control, watch } = useForm<{ search: string }>({
    defaultValues: {
      search: "",
    },
  });

  const searchValue = watch("search"); // Get search value from the form

  // State to store the documents
  const {
    data: documents,
    isLoading,
    isError,
    error,
    refetch,
  } = useDocuments(
    1,
    searchValue, // Pass search value to the useDocuments hook
  );

  const handleSearch = () => {
    refetch(); // Trigger documents fetching with the updated search value
  };

  return (
    <>
      <View className="flex flex-row justify-between py-4 items-center">
        <Text className="text-xl font-bold text-gray-700">Meine Dokumente</Text>
        <View className="flex flex-row gap-1">
          <Input
            control={control}
            name="search"
            aria-label="Dokument suchen"
            placeholder="Dokument suchen"
            type="search"
            className="max-w-40"
          />
          <Button onPress={handleSearch}>
            {/* Trigger search on button press */}
            <Search size={20} color="white" />
          </Button>
        </View>
      </View>
      <View className="flex flex-col space-y-2">
        {isLoading && <LoadingIndicator />}
        {isError && <Text>Error... {error.message}</Text>}

        {documents?.map((document) => (
          <View
            key={document.id}
            className="flex flex-row justify-between items-center py-2 hover:bg-gray-100 rounded"
          >
            <Text className="text-primary font-robotoMedium text-lg">
              {document.name}
            </Text>
            {document.id && (
              <View className="flex flex-row">
                <Pressable
                  onPress={async () => {
                    const url = getDocumentDownloadLink(
                      document.profileId,
                      document.id,
                    );
                    if (Platform.OS === "web") {
                      window.open(url, "_blank");
                    } else {
                      await openBrowserAsync(url);
                    }
                  }}
                >
                  <Download size={24} color="#1B6E98" className="grow-0" />
                </Pressable>
              </View>
            )}
          </View>
        ))}
      </View>
    </>
  );
}
