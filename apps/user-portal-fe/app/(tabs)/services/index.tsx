import { DynamicIcon, useServiceList } from "@repo/services";
import { Input, LoadingIndicator, Text } from "@repo/ui";
import { type Href, Link, Stack } from "expo-router";
import { ChevronRight } from "lucide-react-native";
import type { ReactNode } from "react";
import { useForm } from "react-hook-form";
import { Pressable, ScrollView, View } from "react-native";
import { Header } from "@/components/header";

export function Item({
  icon,
  children,
  href,
  isFirst,
}: {
  icon: string;
  children: ReactNode;
  href: Href;
  isFirst?: boolean;
}) {
  return (
    <View
      className={`border-t native:border-t-hairline border-border ${
        isFirst ? "border-t-0 native:border-t-0 border-white" : ""
      } py-2 px-6 flex flex-row items-center`}
    >
      <Link href={href} asChild>
        <Pressable className="flex flex-row items-center w-full gap-4">
          <DynamicIcon
            name={icon}
            size={24}
            color="#1B6E98"
            className="grow-0 shrink-0"
          />
          <View className="grow shrink">
            <Text>{children}</Text>
          </View>
          <ChevronRight size={24} color="#494949" className="grow-0 shrink-0" />
        </Pressable>
      </Link>
    </View>
  );
}

export default function ServicesIndex() {
  const { data, isLoading, isError, error } = useServiceList();
  const { control, watch } = useForm({
    defaultValues: { search: "" },
  });
  const searchValue = watch("search");

  let domains = data ?? [];

  if (searchValue) {
    domains = domains.filter((domain) =>
      domain.title.toLowerCase().includes(searchValue.toLowerCase()),
    );
  }

  return (
    <>
      <Stack.Screen
        options={{
          title: "Alle Dienste",
          header({ navigation, options }) {
            return (
              <Header
                onPress={() => navigation.goBack()}
                more={
                  <Input
                    placeholder="Domäne suchen..."
                    name="search"
                    control={control}
                    aria-label="Domäne suchen..."
                    className="mt-4"
                    type="search"
                  />
                }
              >
                {options.title}
              </Header>
            );
          },
          contentStyle: { backgroundColor: "white" },
        }}
      />
      <ScrollView className="py-5">
        {isLoading && <LoadingIndicator />}
        {isError && <Text>Error... {error.message}</Text>}

        {domains.map((domain, index) => (
          <Item
            icon={domain.icon}
            href={`/services/${domain.slug}`}
            isFirst={index === 0}
            key={domain.slug}
          >
            {domain.title}
          </Item>
        ))}
      </ScrollView>
    </>
  );
}
