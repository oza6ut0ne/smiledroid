const getVarNamePattern = new RegExp("(.*?);? *}?$");


export function getVarName<TVariable>(getVarFunc: () => TVariable, depth: number = 1): string {
    const m = getVarNamePattern.exec(getVarFunc + "");
    if (m == null)
        return '';

    const memberParts = m[1].split('.');
    return memberParts.slice(-depth).join('.');
}

export function noTruncSplit(s: string, sep: string, limit: number) {
    const parts = s.split(sep, limit);
    parts.push(s.slice(parts.join('').length + (sep.length * limit)));
    return parts;
}

export function base64decode(encoded: string) {
    const binaryString = atob(encoded);
    const bytes = Uint8Array.from(binaryString, binaryChar => binaryChar.charCodeAt(0));
    return new TextDecoder().decode(bytes);
}
