import { RoomStatusType } from "../types/room-status.type";

export interface IWebSocketResponse {
    roomStatus: RoomStatusType,
    message: string,
    data: any
}