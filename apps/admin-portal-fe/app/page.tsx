"use client";

import {
  Button,
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  Text,
} from "@repo/ui";

export default function Web() {
  return (
    <div className="text-center">
      <h1>Admin Portal</h1>
      <div className="mt-5">
        <Dialog>
          <DialogTrigger asChild>
            <Button>
              <Text>Boop</Text>
            </Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Beep</DialogTitle>
            </DialogHeader>
          </DialogContent>
        </Dialog>
      </div>
    </div>
  );
}
