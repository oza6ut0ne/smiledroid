import { Comment, RendererInfo } from './types';


export default interface IAndroidApi {
    notifyCommentArrivedToLeftEdge: (comment: Comment, windowIndex: number) => void;
    requestDuration: (callback: (duration: number) => void) => void;
    requestDefaultDuration: (callback: (duration: number) => void) => void;
    requestTextColorStyle: (callback: (style: string) => void) => void;
    requestTextStrokeStyle: (callback: (style: string) => void) => void;
    requestNewlineEnabled: (callback: (isEnabled: boolean) => void) => void;
    requestIconEnabled: (callback: (isEnabled: boolean) => void) => void;
    requestInlineImgEnabled: (callback: (isEnabled: boolean) => void) => void;
    requestImgEnabled: (callback: (isEnabled: boolean) => void) => void;
    requestVideoEnabled: (callback: (isEnabled: boolean) => void) => void;
    requestRoundIconEnabled: (callback: (isEnabled: boolean) => void) => void;
    onCommentReceived: (callback: (comment: Comment, rendererInfo: RendererInfo) => void) => void;
    onTogglePause: (callback: () => void) => void;
    onDurationUpdated: (callback: (duration: number) => void) => void;
    onUpdateNewlineEnabled: (callback: (isEnabled: boolean) => void) => void;
    onUpdateIconEnabled: (callback: (isEnabled: boolean) => void) => void;
    onUpdateInlineImgEnabled: (callback: (isEnabled: boolean) => void) => void;
    onUpdateImgEnabled: (callback: (isEnabled: boolean) => void) => void;
    onUpdateVideoEnabled: (callback: (isEnabled: boolean) => void) => void;
    onUpdateRoundIconEnabled: (callback: (isEnabled: boolean) => void) => void;
}

declare global {
    interface Window {
      api: IAndroidApi;
    }
}
