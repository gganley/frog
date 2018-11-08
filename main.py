from harma import harma


if __name__ == '__main__':
    sxx, a, t = harma('/Users/gganley/Processed Frog/output.wav')
    for x in t:
        print(((x[2] - x[0]) / 44100) * 1000)
