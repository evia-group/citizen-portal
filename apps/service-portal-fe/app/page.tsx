"use client";
import { useApplicationList } from "@repo/applications";
import { statusMap } from "@repo/services/models/dog-application";
import {
  Button,
  H1,
  LoadingIndicator,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
  Text,
} from "@repo/ui";
import { UserCog } from "lucide-react";
import Link from "next/link";

export default function Web() {
  const { data, isLoading, isError, error } = useApplicationList();

  const columnWidths = [160, 200, 160, 160, 160, 160, 160, 160] as const;
  const sum = columnWidths.reduce((acc, width) => acc + width, 0);

  function colStyles(index: number) {
    const columnWidth = columnWidths[index];
    const percent = (columnWidth / sum) * 100;
    return { width: `${percent}%`, minWidth: columnWidth } as const;
  }

  if (isLoading) {
    return <LoadingIndicator />;
  }

  if (isError) {
    return <span>Error... {error.message}</span>;
  }

  return (
    <div className="text-center p-4">
      <H1 icon={UserCog} className="mb-8">
        Eingegangene Anträge
      </H1>
      <Table aria-label="Service Tabelle">
        <TableHeader>
          <TableRow>
            <TableHead style={colStyles(0)}>
              <Text>ID</Text>
            </TableHead>
            <TableHead style={colStyles(1)}>
              <Text>Dienstbezeichnung</Text>
            </TableHead>
            <TableHead style={colStyles(2)}>
              <Text>Eingangsdatum</Text>
            </TableHead>
            <TableHead style={colStyles(3)}>
              <Text>Vorname/n</Text>
            </TableHead>
            <TableHead style={colStyles(4)}>
              <Text>Nachname</Text>
            </TableHead>
            <TableHead style={colStyles(5)}>
              <Text>Geburtsdatum</Text>
            </TableHead>
            <TableHead style={colStyles(6)}>
              <Text>Status</Text>
            </TableHead>
            <TableHead style={colStyles(7)}>
              <Text>Details</Text>
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {data
            ? data.map((item) => (
                <TableRow key={item.id}>
                  <TableCell style={colStyles(0)}>
                    <Text>{item.id}</Text>
                  </TableCell>
                  <TableCell style={colStyles(1)}>
                    <Text> {item.service.name ?? "-"} </Text>
                  </TableCell>
                  <TableCell style={colStyles(2)}>
                    <Text> {item.createdDate ?? "-"}</Text>
                  </TableCell>
                  <TableCell style={colStyles(3)}>
                    <Text> {item.profile.firstName ?? "-"}</Text>
                  </TableCell>
                  <TableCell style={colStyles(4)}>
                    <Text> {item.profile.lastName ?? "-"}</Text>
                  </TableCell>
                  <TableCell style={colStyles(5)}>
                    <Text> {item.profile.birthDate ?? "-"}</Text>
                  </TableCell>
                  <TableCell style={colStyles(6)}>
                    <Text>{statusMap[item.status]}</Text>
                  </TableCell>
                  <TableCell style={colStyles(7)}>
                    <Button asChild>
                      <Link href={`/${item.id}`}>
                        <Text>Details</Text>
                      </Link>
                    </Button>
                  </TableCell>
                </TableRow>
              ))
            : null}
        </TableBody>
      </Table>
    </div>
  );
}
