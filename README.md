# PANic

PANic is a Clojure command line tool implementing a partially known plaintext attack against unsalted SHA1 hashed primary account numbers (PAN's). This attack assumes the attacker chooses which [IIN (Issuer Identification Number)](https://en.wikipedia.org/wiki/Bank_card_number#Issuer_identification_number_.28IIN.29) to target, thereby greatly reducing the number of hashes that needs to be computed and compared.

This tool has been created primarily as a learning exercise, but also to demonstrate how easy it is to recover PAN's from SHA1 hashes when you can make assumptions about the card issuer. No adwanced techniques (rainbow tables, GPU etc) are used - which should only emphasize that this is a highly practical attack.

## Usage

```
~/d/p/panic ❯❯❯ java -jar target/panic-0.1.0-SNAPSHOT-standalone.jar
PANic. Tool for bruteforcing of primary account number (PAN) SHA1 hashes.

Usage: java <panic jar> [options]

Options:
  -i, --iin IIN              IIN range
  -t, --nthreads THREADS  4  Number of threads
  -f, --file FILE            Input file containing SHA1 hashes
  -h, --help

~/d/p/panic ❯❯❯
```

## Example

In this example, 100000 PAN's with the IIN '112233' are bruteforced.

```
~/d/p/panic ❯❯❯ time java -jar target/panic-0.1.0-SNAPSHOT-standalone.jar -i 112233 -t 4 -f resources/testdata
[000001/100000] 285031c26c5634be0a6e48104b190d7798a3fafb -> 1122332500106941
[000002/100000] de1b87a100d0eefd880e01107677e460245d863c -> 1122337500099079
[000003/100000] 8c145b9231788f636fb8d13df9ef7aa1d7bf223e -> 1122330000141202
[000004/100000] 8b5ebf5e9acab4d735c4fb761c64a368c82c28a8 -> 1122332500176100
[000005/100000] 7bb502c3249f801bba8464fc86c74e8dd17476ff -> 1122330000186710
[000006/100000] 5cab191ce34f6c16dedc7a32eb72cfa0c0561907 -> 1122332500180128
[000007/100000] d5c926b8f7ee1ac25977f5e1323c92ade6ebe35d -> 1122330000221459
[000008/100000] 6284815431e9d94e0dfbef8b164e4d970cbc6944 -> 1122337500231375
[000009/100000] 77c5dfa60158000d4e5059d4912a6ab7830996ae -> 1122335000263914
[000010/100000] 6510a78fc9f1f9059a86878ebf2fd41846a2b145 -> 1122332500319791
<...snip...>
[099991/100000] aeba41ab8c11fb27593b8dbef6430d47027f8019 -> 1122339998967213
[099992/100000] 275a35659cd987f9823ea39d420e896dae64268c -> 1122339999287595
[099993/100000] c7e79fdfd8d2ae5641f75ee5a7d5fd0cf7abf8b9 -> 1122339999291670
[099994/100000] 6a374d8723423c9629725dffedf62f94a6656f81 -> 1122339999384855
[099995/100000] aa7d917d3e036d6932a695b8e1d50cf973f31fe8 -> 1122339999424735
[099996/100000] 8881677be16beb89cdbc1db1e867f43663b3584e -> 1122339999463238
[099997/100000] 315de634a7aee69e5f0d98ee4e728b7b805ce73b -> 1122339999662128
[099998/100000] 40f754b2ed30c1a60bc4ab1d6f733210511b944f -> 1122339999680468
[099999/100000] bd23cb06407f1290f9c1bd79e6bf7b81a1479194 -> 1122339999753752
[100000/100000] 122acc434d08843e282b1fd9fddd57ab177358fb -> 1122339999784146
java -jar target/panic-0.1.0-SNAPSHOT-standalone.jar -i 112233 -t 4 -f   4549.39s user 33.90s system 329% cpu 23:08.88 total

```

## License

Copyright © 2014 Gunnar Helgason

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
