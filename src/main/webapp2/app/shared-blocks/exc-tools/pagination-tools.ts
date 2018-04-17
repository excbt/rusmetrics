export const defaultPageSizeOptions = [10, 20, 50];
export const defaultPageSize = defaultPageSizeOptions[0];

export class ExcPageSize {
    constructor(
        public page = 0,
        public size = defaultPageSizeOptions[0]
    ) { }
}

export class ExcPageSorting {
    constructor(
        public fieldName = 'id',
        public fieldOrder = 'asc'
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
