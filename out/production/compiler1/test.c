/* This Program is designed by 20373649. When you meet some problems, please contact me. */
/* This Program's difficulty level is B. */
/* Main Content:  */
/* Decl */
const int SIZE = 1000;
const int qpow_MOD = 10089;
int arr[1000 + 2];
int temp[1000 + 2];

/* FuncDef */
void divide_and_conquer_sort(int n, int a[], int s) {
    if (n == 1) {
        return;
    } else if (n == 2) {
        if (a[s+0] > a[s+1]) {
            int tmp = a[s+0];
            a[s+0] = a[s+1];
            a[s+1] = tmp;
        }
    } else {
        // Divide
        int first_n = n / 2;
        int last_n = n - first_n;
        divide_and_conquer_sort(first_n, a, s); // transfer size-unknown array a[]
        divide_and_conquer_sort(last_n, a, s+first_n);

        int tpos = 0; // tmp_pos
        int f_pos = 0;
        int l_pos = 0;
        int last_s = s + first_n;
        while (1) {
            if (f_pos == first_n-1) {
                if (l_pos == last_n-1) {
                    break;
                }
            }

            if(f_pos == first_n-1) {
                temp[tpos] = a[last_s+l_pos];
                tpos = tpos + 1;
                l_pos = l_pos + 1;
            }
            else if(l_pos == last_n-1) {
                temp[tpos] = a[s+f_pos];
                tpos = tpos + 1;
                f_pos = f_pos + 1;
            }
            else {
                if (a[s+f_pos] < a[last_s+l_pos]) {
                    temp[tpos] = a[s+f_pos];
                    tpos = tpos + 1;
                    f_pos = f_pos + 1;
                } else {
                    temp[tpos] = a[last_s+l_pos];
                    tpos = tpos + 1;
                    l_pos = l_pos + 1;
                }
            }
        }

        int i = 0; // copy temp to a
        while (i != n) {
            a[s+i] = temp[i];
            i = i + 1;
        }
    }
    return;
}

int quick_pow(int a, int p, int mod) {
    int pp = p;
    int A = a;
    int ans = 1;
    while (pp != 0) {
        if (pp % 2 == 1) {
            ans = ans * A % mod;
        }
        A = A * A % mod;
        pp = pp / 2; // use right shift
    }
    return ans;
}

int hash(int n, int a[]) {
    const int Base = 53;
    const int MOD = 9999973;
    int hash = 0;

    int i = 0;
    while (i != n) {
        hash = (hash * Base + a[i]) % MOD;
        i = i + 1;
    }
    return hash;
}

/* MainFuncDef */
int main() {
    printf("20373649\n"); // print1

    // design a Divide-and-Conquer Sort
    int cnt = 0;
    while (cnt != 5) {
        int n;
        n = 12;;
        int i = 0;
        while (i != n) {
            arr[i] = getint(); // the elem of arr should be < 50
            i = i+1;
        }

        divide_and_conquer_sort(n, arr, 0);
        printf("The hash of the sorted array is %d.\n", hash(n, arr)); // print2~6

        cnt = cnt + 1;
    }

//    int qpow_ans[4];
//    qpow_ans[0] = quick_pow(34, 89, qpow_MOD);
//    qpow_ans[1] = quick_pow(59, 122, qpow_MOD);
//    qpow_ans[2] = quick_pow(13, 3444, qpow_MOD);
//    qpow_ans[3] = quick_pow(11, 987, qpow_MOD);
//
//    printf("The answer of quick_pow is: %d %d %d %d.\n",
//        qpow_ans[0], qpow_ans[1],
//        qpow_ans[2], qpow_ans[3]); // print7
//
//    int a;
//    a = getint();
//    int b;
//    b = getint();
//    int c;
//    c = getint();
//    if (a < b == b < c) {
//        printf("a < b and b < c are equal.\n"); // print8
//    }
//
//    {{{{{  }}}}}
//
//    213 * 789;
//
//    2334 - 2378;
//
//    int tmp_val;
//    tmp_val = getint();
//    tmp_val = tmp_val * 8 * 16 * 256;
//    printf("The num I input is: %d\n", tmp_val / 8 / 16 / 256); // print9, to test optimization
//
//    int d = ((12 * tmp_val) / 7 * 23 - 2222 + 34) % 6 * 999 - 1222;
//    printf("After a complex calculation: d = %d\n", d); // print10
    return 0;
}
