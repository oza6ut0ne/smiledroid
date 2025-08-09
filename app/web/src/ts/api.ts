import { Comment, OverLimitComments, RendererInfo } from './types';

export class Api {
    static notifyCommentArrivedToLeftEdge(comment: Comment, windowIndex: number) {
        // nop.
    }

    static requestDuration(callback: (duration: number) => void) {
        // @ts-ignore
        const result = jsObject.requestDuration() as number;
        callback(result);
    }

    static requestDefaultDuration(callback: (duration: number) => void) {
        // @ts-ignore
        const result = jsObject.requestDefaultDuration() as number;
        callback(result);
    }

    static requestMaxCommentsOnDisplay(callback: (maxComments: number) => void) {
        // @ts-ignore
        const result = jsObject.requestMaxCommentsOnDisplay() as number;
        callback(result);
    }

    static requestFontSize(callback: (size: string) => void) {
        // @ts-ignore
        const result = jsObject.requestFontSize() as string;
        callback(result);
    }

    static requestTextColorStyle(callback: (style: string) => void) {
        // @ts-ignore
        const result = jsObject.requestTextColorStyle() as string;
        callback(result);
    }

    static requestTextStrokeStyle(callback: (style: string) => void) {
        // @ts-ignore
        const result = jsObject.requestTextStrokeStyle() as string;
        callback(result);
    }

    static requestOverLimitComments(callback: (value: OverLimitComments) => void) {
        // @ts-ignore
        const result = jsObject.requestOverLimitComments() as OverLimitComments;
        callback(result);
    }

    static requestNewlineEnabled(callback: (isEnabled: boolean) => void) {
        // @ts-ignore
        const result = jsObject.requestNewlineEnabled() as boolean;
        callback(result);
    }

    static requestIconEnabled(callback: (isEnabled: boolean) => void) {
        // @ts-ignore
        const result = jsObject.requestIconEnabled() as boolean;
        callback(result);
    }

    static requestInlineImgEnabled(callback: (isEnabled: boolean) => void) {
        // @ts-ignore
        const result = jsObject.requestInlineImgEnabled() as boolean;
        callback(result);
    }

    static requestImgEnabled(callback: (isEnabled: boolean) => void) {
        // @ts-ignore
        const result = jsObject.requestImgEnabled() as boolean;
        callback(result);
    }

    static requestVideoEnabled(callback: (isEnabled: boolean) => void) {
        // @ts-ignore
        const result = jsObject.requestVideoEnabled() as boolean;
        callback(result);
    }

    static requestRoundIconEnabled(callback: (isEnabled: boolean) => void) {
        // @ts-ignore
        const result = jsObject.requestRoundIconEnabled() as boolean;
        callback(result);
    }

    static onCommentReceived(callback: (comment: Comment, rendererInfo: RendererInfo) => void) {
        // nop.
    }

    static onTogglePause(callback: () => void) {
        // nop.
    }

    static onUpdateOverLimitComments(callback: (value: OverLimitComments) => void) {
        // nop.
    }

    static onDurationUpdated(callback: (duration: number) => void) {
        // nop.
    }

    static onUpdateNewlineEnabled(callback: (isEnabled: boolean) => void) {
        // nop.
    }

    static onUpdateIconEnabled(callback: (isEnabled: boolean) => void) {
        // nop.
    }

    static onUpdateInlineImgEnabled(callback: (isEnabled: boolean) => void) {
        // nop.
    }

    static onUpdateImgEnabled(callback: (isEnabled: boolean) => void) {
        // nop.
    }

    static onUpdateVideoEnabled(callback: (isEnabled: boolean) => void) {
        // nop.
    }

    static onUpdateRoundIconEnabled(callback: (isEnabled: boolean) => void) {
        // nop.
    }
}
