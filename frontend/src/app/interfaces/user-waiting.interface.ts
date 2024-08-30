import { UserStatusType } from "../types/user-status.type";

export interface IUserWaiting {
    userId: number,
    username: string,
    status: UserStatusType
}