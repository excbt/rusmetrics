export class ExcPageSize {
    constructor(
        public page: number,
        public size: number
    ) { }
}

export class ExcPageSorting {
    constructor(
        public fieldName: string,
        public fieldOrder: string
    ) {}

    orderString(): string {
        if (this.fieldOrder && this.fieldName) {
            return this.fieldName.concat(',', this.fieldOrder);
        } else {
            return '';
        }
    }
}

export class ExcPage<T> {
    constructor(
        public content: T[],
        public size: number,
        public totalElements: number,
        public totalPages: number
    ) {}
}
